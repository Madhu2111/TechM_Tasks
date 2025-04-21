package com.showvault.repository;

import com.showvault.model.UserNotification;
import com.showvault.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {
    
    List<UserNotification> findByUserOrderByCreatedAtDesc(User user);
    
    Page<UserNotification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    List<UserNotification> findByUserAndReadOrderByCreatedAtDesc(User user, boolean read);
    
    long countByUserAndRead(User user, boolean read);
    
    @Modifying
    @Query("UPDATE UserNotification n SET n.read = true, n.readAt = CURRENT_TIMESTAMP WHERE n.user = ?1 AND n.read = false")
    int markAllAsRead(User user);
    
    @Modifying
    @Query("DELETE FROM UserNotification n WHERE n.user = ?1 AND n.read = true")
    int deleteAllReadNotifications(User user);
    
    List<UserNotification> findByUserAndTypeOrderByCreatedAtDesc(User user, UserNotification.NotificationType type);
    
    List<UserNotification> findByRelatedIdAndRelatedType(Long relatedId, String relatedType);
}