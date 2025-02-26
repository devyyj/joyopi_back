package com.joyopi.backend.user.service;

import com.joyopi.backend.common.exception.CustomException;
import com.joyopi.backend.oauth.OAuthService;
import com.joyopi.backend.user.domain.User;
import com.joyopi.backend.user.dto.UserRequestDto;
import com.joyopi.backend.user.dto.UserResponseDto;
import com.joyopi.backend.user.repository.UserEntity;
import com.joyopi.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final OAuthService oAuthService;

    @Override
    public UserResponseDto getUserById(Long id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found with id: " + id));
        return userMapper.toResponseDto(userMapper.toDomain(entity));  // 변환 후 DTO 반환
    }

    @Override
    public UserResponseDto updateUser(Long id, UserRequestDto requestDto) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found with id: " + id));

        // 닉네임 업데이트
        entity.setNickname(requestDto.nickname());
        UserEntity updatedEntity = userRepository.save(entity);
        return userMapper.toResponseDto(userMapper.toDomain(updatedEntity));  // 변환 후 DTO 반환
    }

    @Override
    public void deleteUser(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found with id: " + id));
        oAuthService.unlink(userEntity.getOauthId(), userEntity.getOauthProvider());
        userRepository.deleteById(id);
    }

    @Override
    public User createOrUpdateUser(String oauthProvider, String oauthId) {
        UserEntity entity = userRepository.findByOauthProviderAndOauthId(oauthProvider, oauthId)
                .orElse(null);

        if (entity == null) {
            // 사용자 없으면 새로 생성
            entity = new UserEntity();
            entity.setNickname(oauthProvider + oauthId);
            entity.setRole("ROLE_USER");
            entity.setOauthProvider(oauthProvider);
            entity.setOauthId(oauthId);
        }

        entity.setLastLogin(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        userRepository.save(entity);

        // UserEntity -> User 변환 후 반환
        return userMapper.toDomain(entity);  // 도메인 객체 반환
    }
}
