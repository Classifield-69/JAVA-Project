package com.gamecollection.service;

import com.gamecollection.dto.UserDto;
import com.gamecollection.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll(Sort.by("id")).stream()
                .map(UserDto::from)
                .toList();
    }
}