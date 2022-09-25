package com.dayone.service;

import com.dayone.exception.impl.NoCompanyException;
import com.dayone.model.Company;
import com.dayone.model.Dividend;
import com.dayone.model.ScrapedResult;
import com.dayone.model.constants.CacheKey;
import com.dayone.persist.CompanyRepository;
import com.dayone.persist.DividendRepository;
import com.dayone.persist.entity.CompanyEntity;
import com.dayone.persist.entity.DividendEntity;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class FinanceService {

	private final CompanyRepository companyRepository;
	private final DividendRepository dividendRepository;

	@Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
	public ScrapedResult getDividendByCompanyName(String companyName) {

		CompanyEntity company = this.companyRepository.findByName(companyName)
			.orElseThrow(NoCompanyException::new);
		List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(
			company.getId());

		List<Dividend> dividends = new ArrayList<>();
		for (var entity : dividendEntities) {
			Dividend dividend = new Dividend();
			dividend.setDate(entity.getDate());
			dividend.setDividend(entity.getDividend());
			dividends.add(dividend);
		}

		return new ScrapedResult(new Company(company.getTicker(), company.getName()),
			dividends);
	}
}
