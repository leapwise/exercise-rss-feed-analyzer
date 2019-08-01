package hr.leapwise.exercise.domain.engine.analyisis.model.custom.abstracts;

import hr.leapwise.exercise.domain.engine.analyisis.model.custom.impl.CustomDescriptor;
import hr.leapwise.exercise.domain.engine.analyisis.model.custom.impl.CustomItem;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CustomDescriptorsHolder {

     protected Map<CustomDescriptor, Set<CustomItem>> significantDescriptors = new HashMap<>();

     public Map<CustomDescriptor, Set<CustomItem>> getSignificantDescriptors() {
        return Collections.unmodifiableMap(significantDescriptors);
    }
}
