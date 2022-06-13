package net.thumbtack.school.buscompany.service;

import net.thumbtack.school.buscompany.ApplicationProperties;
import net.thumbtack.school.buscompany.dto.request.*;
import net.thumbtack.school.buscompany.dto.response.AdminDtoResponse;
import net.thumbtack.school.buscompany.dto.response.ClientDtoResponse;
import net.thumbtack.school.buscompany.dto.response.ResponseWithSessionId;
import net.thumbtack.school.buscompany.dto.response.UserDtoResponse;
import net.thumbtack.school.buscompany.error.BusAppErrorCode;
import net.thumbtack.school.buscompany.error.BusAppException;
import net.thumbtack.school.buscompany.mapper.AdminMapper;
import net.thumbtack.school.buscompany.mapper.ClientMapper;
import net.thumbtack.school.buscompany.model.*;
import net.thumbtack.school.buscompany.repository.AdminRepository;
import net.thumbtack.school.buscompany.repository.ClientRepository;
import net.thumbtack.school.buscompany.repository.UserRepository;
import net.thumbtack.school.buscompany.repository.UserSessionRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.UUID.randomUUID;

@Service
public class UserService extends ServiceBase {

    private final UserRepository userRepository;

    private final ClientRepository clientRepository;

    private final AdminRepository adminRepository;

    public UserService(UserSessionRepository userSessionRepository, UserRepository userRepository,
                       ClientRepository clientRepository, AdminRepository adminRepository,
                       ApplicationProperties applicationProperties) {
        super(userSessionRepository, applicationProperties);
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.adminRepository = adminRepository;
    }

    public ResponseWithSessionId<AdminDtoResponse> insertAdmin(AdminDtoRequest request) throws BusAppException {
        Admin admin;
        try {
            admin = adminRepository.save(AdminMapper.INSTANCE.dtoToAdmin(request));
        } catch (DataIntegrityViolationException ex) {
            throw new BusAppException(BusAppErrorCode.LOGIN_ALREADY_EXIST);
        }
        return new ResponseWithSessionId<>(AdminMapper.INSTANCE.adminToDto(admin), generateAndSaveSessionId(admin));
    }

    public ResponseWithSessionId<ClientDtoResponse> insertClient(ClientDtoRequest request) throws BusAppException {
        Client client;
        try {
            client = clientRepository.save(ClientMapper.INSTANCE.dtoToClient(request));
        } catch (DataIntegrityViolationException ex) {
            throw new BusAppException(BusAppErrorCode.LOGIN_ALREADY_EXIST);
        }
        return new ResponseWithSessionId<>(ClientMapper.INSTANCE.clientToDto(client), generateAndSaveSessionId(client));
    }

    public ResponseWithSessionId<UserDtoResponse> login(LoginDtoRequest request) throws BusAppException {
        User user = userRepository.findByLoginIgnoreCaseAndPassword(request.getLogin(), request.getPassword());
        if (user == null) {
            throw new BusAppException(BusAppErrorCode.WRONG_LOGIN_PASSWORD);
        }
        if (user instanceof Admin) {
            return new ResponseWithSessionId<>(AdminMapper.INSTANCE.adminToDto((Admin) user), generateAndSaveSessionId(user));
        }
        return new ResponseWithSessionId<>(ClientMapper.INSTANCE.clientToDto((Client) user), generateAndSaveSessionId(user));
    }

    public void logout(String sessionId) throws BusAppException {
        UserSession userSession = findActiveSession(sessionId);
        if (userSession == null) {
            throw new BusAppException(BusAppErrorCode.SESSION_NOT_FOUND);
        }
        userSessionRepository.delete(userSession);
    }

    @Transactional
    public void deleteUser(String sessionId) throws BusAppException {
        if (userRepository.deleteBySessionId(sessionId) == 0) {
            throw new BusAppException(BusAppErrorCode.SESSION_NOT_FOUND);
        }
    }

    public UserDtoResponse getUserInfo(String sessionId) throws BusAppException {
        User user = getUserBySessionId(sessionId);
        if (user instanceof Admin) {
            return AdminMapper.INSTANCE.adminToDto((Admin) user);
        }
        return ClientMapper.INSTANCE.clientToDto((Client) user);
    }

    public List<ClientDtoResponse> getAllClients(String sessionId) throws BusAppException {
        if (!(getUserBySessionId(sessionId) instanceof Admin)) {
            throw new BusAppException(BusAppErrorCode.NO_PERMISSION);
        }
        return clientRepository.findAll().stream().map(ClientMapper.INSTANCE::clientToDto).collect(Collectors.toList());
    }

    public AdminDtoResponse editAdmin(String sessionId, AdminEditDtoRequest request) throws BusAppException {
        User user = getUserBySessionId(sessionId);
        if (!(user instanceof Admin)) {
            throw new BusAppException(BusAppErrorCode.NO_PERMISSION);
        }
        if (!Objects.equals(user.getPassword(), request.getOldPassword())) {
            throw new BusAppException(BusAppErrorCode.WRONG_OLD_PASSWORD);
        }
        Admin admin = (Admin) user;
        admin.setFirstName(request.getFirstName());
        admin.setLastName(request.getLastName());
        admin.setPatronymic(request.getPatronymic());
        admin.setPosition(request.getPosition());
        admin.setPassword(request.getNewPassword());
        adminRepository.save(admin);
        return AdminMapper.INSTANCE.adminToDto(admin);
    }

    public ClientDtoResponse editClient(String sessionId, ClientEditDtoRequest request) throws BusAppException {
        User user = getUserBySessionId(sessionId);
        if (!(user instanceof Client)) {
            throw new BusAppException(BusAppErrorCode.NO_PERMISSION);
        }
        if (!Objects.equals(user.getPassword(), request.getOldPassword())) {
            throw new BusAppException(BusAppErrorCode.WRONG_OLD_PASSWORD);
        }
        Client client = (Client) user;
        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        client.setPatronymic(request.getPatronymic());
        client.setPassword(request.getNewPassword());
        client.setPhone(request.getPhone());
        client.setEmail(request.getEmail());
        clientRepository.save(client);
        return ClientMapper.INSTANCE.clientToDto(client);
    }

    private String generateAndSaveSessionId(User user) {
        String sessionId = String.valueOf(randomUUID());
        Optional<UserSession> optionalUserSession = userSessionRepository.findById(new UserSessionPK(user));
        if (optionalUserSession.isPresent()) {
            UserSession userSession = optionalUserSession.get();
            userSession.setSessionId(sessionId);
            userSession.setLastActionTime(LocalDateTime.now());
            userSessionRepository.save(userSession);
        } else {
            userSessionRepository.save(new UserSession(new UserSessionPK(user), sessionId, LocalDateTime.now()));
        }
        return sessionId;
    }

}
