package com.jerry.mouse.core;

import com.jerry.mouse.api.Session;

public interface SessionManager {

    Session getSession(String id);
}
