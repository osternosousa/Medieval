package com.medieval.foundation

abstract class Entity {

    var isAvailableToDraw: Boolean = false
    var isSleeping: Boolean = false
    var isOutOfSight: Boolean = false
    var isTerminationInitiated: Boolean = false

    open fun initEntity() { }

    open fun update(dt: Float) { }
    open fun drawOpaque(dt: Float) { }
    open fun drawTransparent(dt: Float) { }

    open fun sleep() { }
    open fun awake() { }

    /** This method is called before init/update/terminate/update/draw
     * and can be used to initialize states that will be manipulated by
     * the following actions. */
    open fun initStates() { }
    /** This method is called after init/update/terminate/update/draw
     * and can be used to set states to specific values after being
     * manipulates by the previous actions. */
    open fun clearStates() { }

    open fun initSleeping() { }
    open fun scheduleEntityTermination() { }
    open fun destroyEntity() { }

    open fun updateViewPort() { }
}