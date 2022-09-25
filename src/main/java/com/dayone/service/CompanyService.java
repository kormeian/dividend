package com.dayone.service;

import com.dayone.exception.impl.NoCompanyException;
import com.dayone.model.Company;
import com.dayone.model.ScrapedResult;
import com.dayone.persist.CompanyRepository;
import com.dayone.persist.DividendRepository;
import com.dayone.persist.entity.CompanyEntity;
import com.dayone.persist.entity.DividendEntity;
import com.dayone.scraper.Scraper;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
@AllArgsConstructor
public class CompanyService {

	private final Trie trie;
	private final Scraper yahooFinanceScraper;
	private final CompanyRepository companyRepository;
	private final DividendRepository dividendRepository;

	public Company save(String ticker) {
		boolean exists = this.companyRepository.existsByTicker(ticker);
		if (exists) {
			throw new RuntimeException("already exists ticker -> " + ticker);
		}

		return this.storeCompanyAndDividend(ticker);
	}

	public Page<CompanyEntity> getAllCompany(Pageable pageable) {

		return this.companyRepository.findAll(pageable);
	}

	private Company storeCompanyAndDividend(String ticker) {
		Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);
		if (ObjectUtils.isEmpty(company)) {
			throw new RuntimeException("failed to scrap ticker -> " + ticker);
		}

		ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company);

		CompanyEntity companyEntity =
			this.companyRepository.save(new CompanyEntity(company));
		List<DividendEntity> dividendEntities =
			scrapedResult.getDividends().stream()
				.map(e -> new DividendEntity(companyEntity.getId(), e))
				.collect(Collectors.toList());
		this.dividendRepository.saveAll(dividendEntities);

		return company;
	}

	public List<String> getCompanyNamesByKeyword(String keyword) {

		PageRequest pageRequest = PageRequest.of(0, 10);
		Page<CompanyEntity> companyEntities =
			this.companyRepository.findByNameStartingWithIgnoreCase(keyword, pageRequest);

		return companyEntities.getContent().stream()
			.map(CompanyEntity::getName)
			.collect(Collectors.toList());
	}

	public void addAutocompleteKeyword(String keyword) {
		this.trie.put(keyword, null);
	}

	public List<String> autocomplete(String keyword) {
		return (List<String>) this.trie.prefixMap(keyword).keySet()
			.stream().collect(Collectors.toList());
	}

	public void deleteAutocompleteKeyword(String keyword) {
		this.trie.remove(keyword);
	}

	public String deleteCompany(String ticker) {
		CompanyEntity companyEntity = this.companyRepository.findByTicker(ticker)
			.orElseThrow(NoCompanyException::new);

		this.dividendRepository.deleteAllByCompanyId(companyEntity.getId());
		this.companyRepository.delete(companyEntity);
		this.deleteAutocompleteKeyword(companyEntity.getName());

		return companyEntity.getName();
	}
}
