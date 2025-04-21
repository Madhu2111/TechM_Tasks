package com.showvault.service.impl;

import com.showvault.model.UserNotification;
import com.showvault.models.User;
import com.showvault.repository.UserNotificationRepository;
import com.showvault.service.UserNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserNotificationServiceImpl implements UserNotificationService {

    @Autowired
    private UserNotificationRepository userNotificationRepository;

    @Override
    public List<UserNotification> getUserNotifications(User user) {
        return userNotificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    public Page<UserNotification> getUserNotificationsPaged(User user, Pageable pageable) {
        return userNotificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

    @Override
    public List<UserNotification> getUnreadNotifications(User user) {
        return userNotificationRepository.findByUserAndReadOrderByCreatedAtDesc(user, false);
    }

    @Override
    public long getUnreadCount(User user) {
        return userNotificationRepository.countByUserAndRead(user, false);
    }

    @Override
    public Optional<UserNotification> getNotificationById(Long id) {
        return userNotificationRepository.findById(id);
    }

    @Override
    @Transactional
    public UserNotification createNotification(UserNotification notification) {
        return userNotificationRepository.save(notification);
    }

    @Override
    @Transactional
    public UserNotification createNotification(User user, String title, String message, UserNotification.NotificationType type) {
        UserNotification notification = new UserNotification(user, title, message, type);
        return userNotificationRepository.save(notification);
    }

    @Override
    @Transactional
    public UserNotification createNotification(User user, String title, String message, UserNotification.NotificationType type, Long relatedId, String relatedType) {
        UserNotification notification = new UserNotification(user, title, message, type);
        notification.setRelatedId(relatedId);
        notification.setRelatedType(relatedType);
        return userNotificationRepository.save(notification);
    }

    @Override
    @Transactional
    public boolean markAsRead(Long id) {
        Optional<UserNotification> notificationOpt = userNotificationRepository.findById(id);
        if (notificationOpt.isPresent()) {
            UserNotification notification = notificationOpt.get();
            notification.markAsRead();
            userNotificationRepository.save(notification);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public int markAllAsRead(User user) {
        return userNotificationRepository.markAllAsRead(user);
    }

    @Override
    @Transactional
    public boolean deleteNotification(Long id) {
        if (userNotificationRepository.existsById(id)) {
            userNotificationRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public int deleteAllReadNotifications(User user) {
        return userNotificationRepository.deleteAllReadNotifications(user);
    }

    @Override
    public List<UserNotification> getNotificationsByType(User user, UserNotification.NotificationType type) {
        return userNotificationRepository.findByUserAndTypeOrderByCreatedAtDesc(user, type);
    }

    @Override
    public List<UserNotification> getNotificationsByRelatedEntity(Long relatedId, String relatedType) {
        return userNotificationRepository.findByRelatedIdAndRelatedType(relatedId, relatedType);
    }
}