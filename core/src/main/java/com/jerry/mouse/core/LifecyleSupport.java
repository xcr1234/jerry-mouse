package com.jerry.mouse.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jerry.mouse.util.Properties;

public abstract class LifecyleSupport {
	

	protected final Log logger = LogFactory.getLog(getClass());

	protected abstract void doInit(Properties properties) throws LifecyleException;

	protected abstract void doStart() throws LifecyleException;

	protected abstract void doDestroy() throws LifecyleException;

	private boolean start;
	private boolean init;
	private boolean destroy;

	private int state = INSTANCE;

	private static final int INSTANCE = 0;
	private static final int INIT = 1;
	private static final int START = 2;
	private static final int DESTROY = 3;

	public String getComponentName() {
        return getClass().getSimpleName();
	}

	protected Properties properties;


	public final boolean initialized() {
		return init;
	}

	public final boolean started() {
		return start;
	}

	public final boolean destroyed() {
		return destroy;
	}

	public final void init(Properties properties) throws LifecyleException {
		if (init) {
			throw new LifecyleException("can't init Component " + getComponentName() + ",already initialized.");
		}
		this.properties = properties;
		state = INIT;
		logger.debug("Component " + getComponentName() + " initializing.");
		try{
			doInit(properties);
		}catch (Exception e){
		    if(e instanceof LifecyleException){
		        throw (LifecyleException)e;
            }
            throw new LifecyleException(e);
        }
		init = true;
		logger.debug("Component " + getComponentName() + " initialized successfully.");
		
	}

	public final void start() throws LifecyleException {
		if (!init) {
			throw new LifecyleException("can't start Component " + getComponentName() + ",not initialized.");
		}
		if (destroy) {
			throw new LifecyleException("can't start Component " + getComponentName() + ",destroyed.");
		}
		if (start) {
			throw new LifecyleException("can't start Component " + getComponentName() + ",already started.");
		}
		state = START;
		logger.debug("Component " + getComponentName() + " starting.");
		try{
            doStart();
        }catch (Exception e){
            if(e instanceof LifecyleException){
                throw (LifecyleException)e;
            }
            throw new LifecyleException(e);
        }
		start = true;
		logger.debug("Component " + getComponentName() + " started successfully.");
		
	}

	public final void destroy() throws LifecyleException {
		if (!init) {
			throw new LifecyleException("can't destroy Component " + getComponentName() + ",not initialized.");
		}
		if (destroy) {
			throw new LifecyleException("can't destroy Component " + getComponentName() + ",already destroyed.");
		}
		if (!start) {
			throw new LifecyleException("can't destroy Component " + getComponentName() + ",not started.");
		}
		state = DESTROY;
		logger.debug("Componet " + getComponentName() + " destroying.");
        try{
            doDestroy();
        }catch (Exception e){
            if(e instanceof LifecyleException){
                throw (LifecyleException)e;
            }
            throw new LifecyleException(e);
        }
		destroy = true;
		logger.debug("Componet " + getComponentName() + " destroyed successfully.");
		
	}

	protected final void newException(String msg) throws LifecyleException {
		throw new LifecyleException("can't " + stateString() + " Component " + getComponentName() + ",nest error is : " + msg + ".");
	}
	
	protected final void newException(String msg,Throwable cause) throws LifecyleException{
		throw new LifecyleException("can't " + stateString() + " Component " + getComponentName() + ",nest error is : " + msg + ".",cause);
	}
	
	protected final void newException(Throwable cause) throws LifecyleException{
		throw new LifecyleException("can't " + stateString() + " Component " + getComponentName() + ",nest exception is : " + cause + ".",cause);
	}

	private String stateString() {
		switch (state) {
		case INIT:
			return "init";
		case START:
			return "start";
		case DESTROY:
			return "destroy";
		default:
			return "INSTANCE";
		}
	}

}
