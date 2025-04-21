package com.showvault.service;

import com.showvault.model.UserNotification;
import com.showvault.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserNotificationService {
    
    List<UserNotification> getUserNotifications(User user);
    
    Page<UserNotification> getUserNotificationsPaged(User user, Pageable pageable);
    
    List<UserNotification> getUnreadNotifications(User user);
    
    long getUnreadCount(User user);
    
    Optional<UserNotification> getNotificationById(Long id);
    
    UserNotification createNotification(UserNotification notification);
    
    UserNotification createNotification(User user, String title, String message, UserNotification.NotificationType type);
    
    UserNotification createNotification(User user, String title, String message, UserNotification.NotificationType type, Long relatedId, String relatedType);
    
    boolean markAsRead(Long id);
    
    int markAllAsRead(User user);
    
    boolean deleteNotification(Long id);
    
    int deleteAllReadNotifications(User user);
    
    List<UserNotification> getNotificationsByType(User user, UserNotification.NotificationType type);
    
    List<UserNotification> getNotificationsByRelatedEntity(Long relatedId, String relatedType);
}