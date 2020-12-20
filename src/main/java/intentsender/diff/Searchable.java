package intentsender.diff;

import java.util.List;

/**
 * Created by vfarafonov on 01.01.2016.
 */
public interface Searchable<K, V> {
	List<K> search(V query);
}
