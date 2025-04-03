package com.learnx.service;

import com.learnx.entity.Assignment;
import com.learnx.entity.CourseRegistration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentNotificationService {

    private final AssignmentService assignmentService;
    private final NotificationService notificationService;
    private final CourseRegistrationService courseRegistrationService;
    private final UserService userService;

    @Transactional
//    @Scheduled(cron = "0 0 0 * * ?") // Run daily at midnight
    @Scheduled(cron = "0 0 1/2 * * ?") // Run every 2 hours starting at 1 AM
//    @Scheduled(cron = "0 */10 * * * ?") // Run every 10 minutes
//    @Scheduled(cron = "0 */5 * * * ?") // Run every 5 minutes
//    @Scheduled(cron = "0 * * * * ?") // Run every 1 minute
    public void checkAssignmentsEndingSoon() {
        log.info("CRON: CHECKING ASSIGNMENTS ENDING");
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Assignment> assignmentsEndingTomorrow = assignmentService.getAssignmentsEndingOn(tomorrow);
        if (assignmentsEndingTomorrow.isEmpty()) {
            return;
        }
        log.info("Found " + assignmentsEndingTomorrow.size() + " assignments ending tomorrow");
        for (Assignment assignment : assignmentsEndingTomorrow) {
            log.info("Sending notification for assignment " + assignment.getId());
            // Initialize the lazy-loaded property within the session
            assignment.getModule().getCourse().getId();
            List<String> usersInCourse = courseRegistrationService.getByCourseId(assignment.getModule().getCourse().getId())
                    .stream().map(CourseRegistration::getEmail).filter(e -> userService.findByEmailIgnoreCase(e).isPresent()).toList();

            for (String email : usersInCourse) {
                notificationService.sendNotification(email, "Bài tập " + assignment.getTitle() + " sắp hết hạn", "/submission/" + assignment.getModule().getCourse().getId() + "/" + assignment.getId());
            }
        }
    }
}