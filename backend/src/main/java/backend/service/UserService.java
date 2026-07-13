package backend.service;

import backend.api.UserDtos;
import backend.domain.User;
import backend.domain.UserSettings;
import backend.exception.BusinessException;
import backend.repository.UserRepository;
import backend.repository.UserSettingsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalTime;

@Service
public class UserService {
    private final UserRepository users;
    private final UserSettingsRepository settings;
    private final ActivityService activityService;

    public UserService(UserRepository users, UserSettingsRepository settings, ActivityService activityService) {
        this.users = users;
        this.settings = settings;
        this.activityService = activityService;
    }

    @Transactional(readOnly = true)
    public UserDtos.UserResponse me(long userId) {
        return toResponse(requireUser(userId));
    }

    @Transactional
    public UserDtos.SettingsResponse updateSettings(long userId, UserDtos.SettingsRequest request) {
        User user = requireUser(userId);
        UserSettings value = settings.findById(userId).orElseGet(() -> new UserSettings(user));
        value.update(request.dailyNewWords(), request.dailyReviewLimit(), request.preferredLevel(),
                LocalTime.parse(request.reminderTime()), request.soundEnabled());
        return toSettings(settings.save(value));
    }

    @Transactional(readOnly = true)
    public UserDtos.UserResponse toResponse(User user) {
        UserSettings value = settings.findById(user.getId())
                .orElseThrow(() -> new IllegalStateException("User settings missing for " + user.getId()));
        return new UserDtos.UserResponse(user.getId(), user.getUsername(), user.getNickname(), user.getEmail(),
                user.getAvatarText(), activityService.continuousDays(user.getId()), toSettings(value));
    }

    @Transactional(readOnly = true)
    public User requireUser(long userId) {
        return users.findById(userId)
                .orElseThrow(() -> new BusinessException(11004, HttpStatus.UNAUTHORIZED, "登录会话失效"));
    }

    public UserDtos.SettingsResponse toSettings(UserSettings value) {
        return new UserDtos.SettingsResponse(value.getDailyNewWords(), value.getDailyReviewLimit(),
                value.getPreferredLevel(), value.getReminderTime().toString(), value.isSoundEnabled());
    }
}
