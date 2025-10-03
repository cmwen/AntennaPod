package de.danoeh.antennapod.event.settings;

public class NormalizeVolumeChangedEvent {
    private final boolean enabled;

    public NormalizeVolumeChangedEvent(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}