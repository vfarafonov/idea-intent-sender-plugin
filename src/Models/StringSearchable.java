package Models;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import diff.Searchable;

/**
 * Created by vfarafonov on 01.01.2016.
 */
public class StringSearchable implements Searchable<String, String> {
	private List<String> data_ = new ArrayList<String>();

	public StringSearchable(List<String> data) {
		this.data_.addAll(data);
	}

	@Override
	public List<String> search(String query) {
		List<String> results = new ArrayList<String>();
		if (query != null) {
			for (String dataItem : data_) {
				if (StringUtils.containsIgnoreCase(dataItem, query)) {
					results.add(dataItem);
				}
			}
		}
		return results;
	}
}
