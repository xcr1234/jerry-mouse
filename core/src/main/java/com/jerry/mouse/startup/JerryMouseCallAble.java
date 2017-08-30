package com.jerry.mouse.startup;

import com.jerry.mouse.server.Server;
import java.util.concurrent.Callable;

public interface JerryMouseCallAble extends Callable<Server> {

    void cancel();

}
