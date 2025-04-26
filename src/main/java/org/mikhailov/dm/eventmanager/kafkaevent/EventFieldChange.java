package org.mikhailov.dm.eventmanager.kafkaevent;

public class EventFieldChange<T> {
    private T oldValue;
    private T newValue;

    public EventFieldChange(T oldValue, T newValue) {
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public EventFieldChange() {}

    public T getOldValue() {
        return oldValue;
    }

    public void setOldValue(T oldValue) {
        this.oldValue = oldValue;
    }

    public T getNewValue() {
        return newValue;
    }

    public void setNewValue(T newValue) {
        this.newValue = newValue;
    }

    @Override
    public String toString() {
        return "EventFieldChange{" +
                "oldValue=" + oldValue +
                ", newValue=" + newValue +
                '}';
    }
}
