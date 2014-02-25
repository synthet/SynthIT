package ru.synthet.synthit.model.service;

import com.foxykeep.datadroid.service.RequestService;
import ru.synthet.synthit.model.RequestFactory;
import ru.synthet.synthit.model.operations.CompsOperation;
import ru.synthet.synthit.model.operations.UsersOperation;

public class RestService extends RequestService {

    @Override
    public Operation getOperationForType(int requestType) {
        switch (requestType) {
            case RequestFactory.REQUEST_USERS:
                return new UsersOperation();
            case RequestFactory.REQUEST_COMPS:
                return new CompsOperation();
            default:
                return null;
        }
    }

}
