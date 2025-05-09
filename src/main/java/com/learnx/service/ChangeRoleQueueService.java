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

    public ChangeRoleQueue save(ChangeRoleQueue changeRoleQueue) {
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
}
