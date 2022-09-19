package com.dayone.service;

import com.dayone.model.Company;
import com.dayone.persist.entity.CompanyEntity;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.Trie;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class CompanyService {

	private final Trie trie;

	public Company save(String ticker) {
		throw new NotYetImplementedException();
	}

	public Page<CompanyEntity> getAllCompany(Pageable pageable) {
		throw new NotYetImplementedException();
	}

	private Company storeCompanyAndDividend(String ticker) {
		throw new NotYetImplementedException();
	}

	public List<String> getCompanyNamesByKeyword(String keyword) {
		throw new NotYetImplementedException();
	}

	public void addAutocompleteKeyword(String keyword) {
		this.trie.put(keyword, null);
	}

	public List<String> autocomplete(String keyword) {
		return (List<String>) this.trie.prefixMap(keyword).keySet()
			.stream()
			.collect(Collectors.toList());
	}

	public void deleteAutocompleteKeyword(String keyword) {
		this.trie.remove(keyword);
	}

	public String deleteCompany(String ticker) {
		throw new NotYetImplementedException();
	}

}
