package intentsender.diff;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by vfarafonov on 01.01.2016.
 */
public class CaseInsensitiveHashSet extends HashSet<String> {
	public CaseInsensitiveHashSet() {
	}

	public CaseInsensitiveHashSet(Collection<? extends String> c) {
		super(c);
	}

	public CaseInsensitiveHashSet(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public CaseInsensitiveHashSet(int initialCapacity) {
		super(initialCapacity);
	}

	@Override
	public boolean contains(Object o) {
		String compareString = (String) o;
		for (String string : this) {
			if (compareString.equalsIgnoreCase(string.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
}
