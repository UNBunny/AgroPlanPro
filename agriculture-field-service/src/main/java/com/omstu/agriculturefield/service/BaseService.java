package com.omstu.agriculturefield.service;

import java.util.List;

public interface BaseService<REQ, RES, ID> {
    List<RES> getAll();
    RES getById(ID id);
    RES create(REQ req);
    RES update(ID id, REQ req);
    void delete(ID id);
}
