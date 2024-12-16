package com.lcaohoanq.nocket.domain.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lcaohoanq.nocket.base.entity.BaseEntity;
import com.lcaohoanq.nocket.domain.avatar.Avatar;
import com.lcaohoanq.nocket.domain.chat.ChatRoom;
import com.lcaohoanq.nocket.domain.friendship.Friendship;
import com.lcaohoanq.nocket.domain.wallet.Wallet;
import com.lcaohoanq.nocket.enums.Gender;
import com.lcaohoanq.nocket.enums.UserRole;
import com.lcaohoanq.nocket.enums.UserStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.checkerframework.common.aliasing.qual.Unique;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity implements UserDetails {
    
    @Unique
    @Email
    @Column(name="email",nullable = false, length = 100)
    private String email;

    @Column(name="password", length = 200)
    @JsonProperty("password")
    private String password;

    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name="is_active", columnDefinition = "boolean default true")
    @JsonProperty("is_active")
    private boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private UserStatus status;

    @Column(name="date_of_birth")
    private String dateOfBirth;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @JsonIgnore
    private List<Avatar> avatars = new ArrayList<>();

    @Unique
    @Column(name="phone_number",nullable = false, length = 100)
    @JsonProperty("phone_number")
    private String phoneNumber;
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference //to prevent infinite loop
    private Wallet wallet;

    @Enumerated(EnumType.ORDINAL)
    @Column(name="role")
    private UserRole role;
    
    @Column(name = "preferred_language")
    private String preferredLanguage;
    
    @Column(name = "preferred_currency")
    private String preferredCurrency;

    @Column(name = "last_login_timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime lastLoginTimestamp;

    @OneToMany(mappedBy = "user1")
    @JsonIgnore
    private List<ChatRoom> initiatedChats = new ArrayList<>();

    @OneToMany(mappedBy = "user2")
    @JsonIgnore
    private List<ChatRoom> receivedChats = new ArrayList<>();
    
    @OneToMany(mappedBy = "user1")
    @JsonIgnore
    private List<Friendship> initiatedFriendships = new ArrayList<>();
    
    @OneToMany(mappedBy = "user2")
    @JsonIgnore
    private List<Friendship> receivedFriendships = new ArrayList<>();

    //Spring Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority("ROLE_"+getRole().name()));
        //authorityList.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        return authorityList;
    }

    //why getUserName() is return email
    //because in the UserDetailsService, we use email to find user
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
