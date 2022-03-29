package coddifier.language;

import java.util.Collections;
import java.util.Set;

public interface Condition {
    default Set<String> getConstantSignature() {
        return Collections.emptySet();
    }
    Set<String> getSignature();
    String toString();
}
