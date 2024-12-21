package com.lcaohoanq.nocket.domain.user;

import com.lcaohoanq.nocket.api.PageResponse;
import com.lcaohoanq.nocket.base.exception.DataNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import com.lcaohoanq.nocket.domain.auth.AuthPort.UpdatePasswordDTO;

public interface IUserService {

    PageResponse<UserPort.UserResponse> fetchUser(Pageable pageable);

    String loginOrRegisterGoogle(String email, String name, String googleId, String avatarUrl)
        throws Exception;

    UserPort.UserResponse findUserById(UUID id) throws DataNotFoundException;

    User findUserByEmail(String email) throws DataNotFoundException;

    List<User> getAllUsers();

    User findByUsername(String username) throws DataNotFoundException;

    Page<User> findAll(String keyword, Pageable pageable) throws Exception;

    void blockOrEnable(UUID userId, Boolean active) throws DataNotFoundException;

    @Transactional
    User updateUser(UUID userId, UserPort.UpdateUserDTO updatedUserDTO) throws Exception;

    @Transactional
    User updateUserBalance(UUID userId, Long payment) throws Exception;

    void bannedUser(UUID userId) throws DataNotFoundException;

    void updatePassword(UpdatePasswordDTO updatePasswordDTO) throws Exception;

    void softDeleteUser(UUID userId) throws DataNotFoundException;

    void restoreUser(UUID userId) throws DataNotFoundException;

    void validateAccountBalance(User user, long basePrice);

    Boolean existsByEmail(String email);

    Boolean existsByPhoneNumber(String phoneNumber);

    Boolean existsById(UUID id);


}
