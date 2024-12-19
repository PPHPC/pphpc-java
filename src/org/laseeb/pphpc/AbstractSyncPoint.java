/*
 * Copyright (c) 2014, 2015, Nuno Fachada
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.laseeb.pphpc;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract simulation synchronization point which implements observer
 * registration and notification and handles synchronization reset.
 * 
 * @author Nuno Fachada
 */
public abstract class AbstractSyncPoint implements ISyncPoint {

	/* List of observers. */
	private List<IControlEventObserver> observers;
	
	/* Simulation event associated with this synchronizer. */
	protected ControlEvent event;
	
	/* Was the simulation interrupted? */
	protected volatile boolean interrupted;
	
	/**
	 * Constructor called by concrete implementations.
	 * 
	 * @param event Simulation event to associate with this simulation
	 * synchronizer. 
	 */
	public AbstractSyncPoint(ControlEvent event) {
		this.event = event;
		this.observers = new ArrayList<IControlEventObserver>();
		this.reset();
	}
	
	/**
	 * @see IControlEventObservable#registerObserver(IControlEventObserver)
	 */
	@Override
	public void registerObserver(IControlEventObserver observer) {
		this.observers.add(observer);
	}
	
	/**
	 * @see ISyncPoint#stopNow()
	 */
	@Override
	public void stopNow() {
		this.interrupted = true;
	}

	/**
	 * @see ISyncPoint#reset()
	 */
	@Override
	public void reset() {
		this.interrupted = false;
	}
	
	/**
	 * @see ISyncPoint#syncNotify(IController)
	 */
	@Override
	public void syncNotify(IController controller) throws InterruptedWorkException {
		
		/* Stop thread if it was interrupted. */
		if (this.interrupted)
			throw new InterruptedWorkException("Interrupted by another thread.");
		
		/* Perform synchronization. */
		this.doSyncNotify(controller);
	}
	
	/**
	 * Perform proper synchronization. Implementations of this method must invoke
	 * {@link #notifyObservers(IController)}.
	 * 
	 * @param controller The simulation controller.
	 * @throws InterruptedWorkException if synchronization is interrupted by another thread.
	 */
	protected abstract void doSyncNotify(IController controller) throws InterruptedWorkException;

	/**
	 * Helper method which notifies the registered observers.
	 * 
	 * @param controller The simulation controller.
	 */
	protected void notifyObservers(IController controller) {
		for (IControlEventObserver o : this.observers) {
			o.update(this.event, controller);
		}
	}
}
