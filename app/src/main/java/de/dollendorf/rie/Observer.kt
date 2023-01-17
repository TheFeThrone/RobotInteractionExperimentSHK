package de.dollendorf.rie

open class Observer {
    protected var observers = ArrayList<ObserverInterface>()

    fun registerObserver(observer: ObserverInterface) {
        observers.add(observer)
    }

    fun unregisterObserver(observer: ObserverInterface) {
        observers.remove(observer)
    }
}