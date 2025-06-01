package com.learnx.service;

import com.learnx.entity.ChangeRoleQueue;
import com.learnx.entity.User;
import com.learnx.entity.enumClass.State;
import com.learnx.exception.ResourceNotFoundException;
import com.learnx.repository.ChangeRoleRepository;
import com.learnx.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ChangeRoleQueueService {
    private final ChangeRoleRepository repository;
    private final UserService userService;
    private final JavaMailService mailService;

    public ChangeRoleQueue save(ChangeRoleQueue changeRoleQueue) {
        if (changeRoleQueue.getStatus() == null) {
            changeRoleQueue.setStatus(State.PENDING);
        }
        User admin = userService.findByEmailIgnoreCase("phucth0710+admin@gmail.com").get();
        String message = "Tài khoản " + changeRoleQueue.getUser().getFullName() + " có yêu cầu thay đổi quyền từ "
                + changeRoleQueue.getOldRole().name() + " thành " + changeRoleQueue.getNewRole().name()
                + ". <br>Vui lòng kiểm tra và xử lý yêu cầu này.";
        mailService.send(admin.getEmail(), buildEmailBody(changeRoleQueue.getUser().getFullName(), message));
        return repository.save(changeRoleQueue);
    }

    public ChangeRoleQueue findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<ChangeRoleQueue> findAll() {
        return repository.findAll();
    }

    public Page<ChangeRoleQueue> findAllPageable(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public ChangeRoleQueue createChangeRoleQueue(ChangeRoleQueue changeRoleQueue) {
        if (repository.findByUserEmail(changeRoleQueue.getUser().getEmail()).isPresent()) {
            throw new RuntimeException("You have already sent a request to change role!");
        }
        return repository.save(changeRoleQueue);
    }

    public Response<?> changeRoleQueueForId(Long changeRoleQueueId) {
        ChangeRoleQueue changeRoleQueue = repository.findById(changeRoleQueueId).orElseThrow(() -> new ResourceNotFoundException("Request not found!"));
        if (changeRoleQueue == null) {
            throw new RuntimeException("Request not found!");
        }
        if (changeRoleQueue.getStatus() != State.PENDING) {
            return Response.builder()
                    .code(200)
                    .success(false)
                    .message("Request has been processed!")
                    .build();
        }
        User user = changeRoleQueue.getUser();
        user.setRole(changeRoleQueue.getNewRole());
        userService.save(user);
        changeRoleQueue.setStatus(State.ACCEPTED);
        repository.save(changeRoleQueue);
        String message = "Tài khoản " + user.getFullName() + " đã được phê duyệt thay đổi quyền từ "
                + changeRoleQueue.getOldRole().name() + " thành " + changeRoleQueue.getNewRole().name();
        mailService.send(user.getEmail(), buildEmailBody(user.getFullName(), message));
        return Response.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Duyệt yêu cầu thành công!")
                .build();
    }

    public Response<?> rejectRequest(Long changeRoleQueueId) {
        ChangeRoleQueue changeRoleQueue = repository.findById(changeRoleQueueId).orElseThrow(() -> new ResourceNotFoundException("Request not found!"));
        if (changeRoleQueue == null) {
            throw new RuntimeException("Request not found!");
        }
        if (changeRoleQueue.getStatus() != State.PENDING) {
            return Response.builder()
                    .code(200)
                    .success(false)
                    .message("Request has been processed!")
                    .build();
        }
        changeRoleQueue.setStatus(State.REJECTED);
        repository.save(changeRoleQueue);
        User user = changeRoleQueue.getUser();
        String message = "Tài khoản " + user.getFullName() + " đã bị từ chối yêu cầu thay đổi quyền từ "
                + changeRoleQueue.getOldRole().name() + " thành " + changeRoleQueue.getNewRole().name();
        mailService.send(user.getEmail(), buildEmailBody(user.getFullName(), message));
        return Response.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Từ chối yêu cầu thành công!")
                .build();
    }

    public ChangeRoleQueue findByUserEmail(String email) {
        return repository.findByUserEmail(email).orElse(null);
    }

    public void deleteByUserEmail(String email) {
        repository.deleteByUserEmail(email);
    }

    private String buildEmailBody(String name, String message) {
        return "<html>\n" +
                "  <head>\n" +
                "    <style>\n" +
                "      body {\n" +
                "        font-family: Arial, sans-serif;\n" +
                "        background-color: #f4f4f4;\n" +
                "        margin: 0;\n" +
                "        padding: 0;\n" +
                "      }\n" +
                "      .container {\n" +
                "        max-width: 600px;\n" +
                "        margin: 50px auto;\n" +
                "        padding: 20px;\n" +
                "        background-color: #fff;\n" +
                "        border-radius: 10px;\n" +
                "        box-shadow: 0 0 10px rgba(0,0,0,0.1);\n" +
                "      }\n" +
                "      h1 {\n" +
                "        color: #333;\n" +
                "      }\n" +
                "      h2 {\n" +
                "        color: #555;\n" +
                "      }\n" +
                "      span {\n" +
                "        color: #ff0000;\n" +
                "        font-weight: bold;\n" +
                "        letter-spacing: 2px;\n" +
                "        font-size: 24px;\n" +
                "      }\n" +
                "    </style>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <div class=\"container\">\n" +
                "      <h1>Xin Chào, " + name + "!</h1>\n" +
                "      <h2> " + message + "</h2>\n" +
                "      <h3>Trân trọng!</h3>\n" +
                "    </div>\n" +
                "  </body>\n" +
                "</html>";
    }
}
