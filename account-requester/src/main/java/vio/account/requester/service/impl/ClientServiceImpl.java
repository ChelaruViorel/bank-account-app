package vio.account.requester.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vio.account.requester.dao.ClientDao;
import vio.account.requester.service.ClientService;

@Slf4j
@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientDao clientDao;

    @Override
    public boolean clientExists(String clientCnp) {
        return clientDao.clientExists(clientCnp);
    }
}
