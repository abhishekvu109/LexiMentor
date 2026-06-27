package com.abhi.saarthi.cashflow.service.impl;

import com.abhi.saarthi.cashflow.entities.Deposit;
import com.abhi.saarthi.cashflow.entities.Earning;
import com.abhi.saarthi.cashflow.entities.Expense;
import com.abhi.saarthi.cashflow.model.ExportFilter;
import com.abhi.saarthi.cashflow.repository.DepositRepository;
import com.abhi.saarthi.cashflow.repository.EarningRepository;
import com.abhi.saarthi.cashflow.repository.ExpenseRepository;
import com.abhi.saarthi.cashflow.service.ExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ExportServiceImpl implements ExportService {

    private final ExpenseRepository expenseRepository;
    private final EarningRepository earningRepository;
    private final DepositRepository depositRepository;

    @Override
    @Transactional(readOnly = true)
    public byte[] exportExpenses(ExportFilter filter) {
        log.info("Exporting expenses with filter: {}", filter);

        Specification<Expense> spec = Specification.unrestricted();
        if (StringUtils.isNotEmpty(filter.getOwner())) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("owner"), filter.getOwner()));
        }
        if (StringUtils.isNotEmpty(filter.getHouseholdRefId())) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.join("household").get("refId"), Long.parseLong(filter.getHouseholdRefId())));
        }
        if (filter.getDateFrom() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("expenseDate"), filter.getDateFrom()));
        }
        if (filter.getDateTo() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("expenseDate"), filter.getDateTo()));
        }

        List<Expense> expenses = expenseRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "expenseDate"));
        log.info("Found {} expenses to export", expenses.size());

        StringBuilder csv = new StringBuilder();
        csv.append("refId,uuid,owner,householdRefId,householdName,householdCurrency,")
           .append("expenseDate,expenseYear,expenseMonth,expenseMonthName,expenseDay,expenseDayOfWeek,expenseDayOfWeekName,")
           .append("description,categoryRefId,categoryName,amount,type,expenseFor,paymentMode,")
           .append("itemCount,items,createdAt\n");

        for (Expense e : expenses) {
            String householdRefId = e.getHousehold() != null ? String.valueOf(e.getHousehold().getRefId()) : "";
            String householdName  = e.getHousehold() != null ? escapeCsv(e.getHousehold().getName()) : "";
            String currency       = e.getHousehold() != null && e.getHousehold().getCurrency() != null
                    ? e.getHousehold().getCurrency().name() : "";
            String categoryRefId  = e.getCategory() != null ? String.valueOf(e.getCategory().getRefId()) : "";
            String categoryName   = e.getCategory() != null ? escapeCsv(e.getCategory().getName()) : "";
            String type           = e.getType() != null ? e.getType().name() : "";
            String expenseFor     = e.getExpenseFor() != null ? e.getExpenseFor().name() : "";
            String paymentMode    = e.getPaymentMode() != null ? e.getPaymentMode().name() : "";

            int year = e.getExpenseDate() != null ? e.getExpenseDate().getYear() : 0;
            int month = e.getExpenseDate() != null ? e.getExpenseDate().getMonthValue() : 0;
            String monthName = e.getExpenseDate() != null
                    ? e.getExpenseDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) : "";
            int day = e.getExpenseDate() != null ? e.getExpenseDate().getDayOfMonth() : 0;
            int dayOfWeek = e.getExpenseDate() != null ? e.getExpenseDate().getDayOfWeek().getValue() : 0;
            String dayOfWeekName = e.getExpenseDate() != null
                    ? e.getExpenseDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH) : "";

            List<String> items = e.getItems();
            int itemCount = items != null ? items.size() : 0;
            String itemsList = items != null ? escapeCsv(String.join("|", items)) : "";

            csv.append(e.getRefId()).append(",")
               .append(escapeCsv(e.getUuid())).append(",")
               .append(escapeCsv(e.getOwner())).append(",")
               .append(householdRefId).append(",")
               .append(householdName).append(",")
               .append(currency).append(",")
               .append(e.getExpenseDate() != null ? e.getExpenseDate() : "").append(",")
               .append(year).append(",")
               .append(month).append(",")
               .append(monthName).append(",")
               .append(day).append(",")
               .append(dayOfWeek).append(",")
               .append(dayOfWeekName).append(",")
               .append(escapeCsv(e.getDescription())).append(",")
               .append(categoryRefId).append(",")
               .append(categoryName).append(",")
               .append(e.getAmount()).append(",")
               .append(type).append(",")
               .append(expenseFor).append(",")
               .append(paymentMode).append(",")
               .append(itemCount).append(",")
               .append(itemsList).append(",")
               .append(e.getCreatedAt() != null ? e.getCreatedAt() : "")
               .append("\n");
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportEarnings(ExportFilter filter) {
        log.info("Exporting earnings with filter: {}", filter);

        Specification<Earning> spec = Specification.unrestricted();
        if (StringUtils.isNotEmpty(filter.getOwner())) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("username"), filter.getOwner()));
        }
        if (filter.getDateFrom() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("depositDate"), filter.getDateFrom()));
        }
        if (filter.getDateTo() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("depositDate"), filter.getDateTo()));
        }

        List<Earning> earnings = earningRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "depositDate"));
        log.info("Found {} earnings to export", earnings.size());

        StringBuilder csv = new StringBuilder();
        csv.append("refId,uuid,username,")
           .append("depositDate,depositYear,depositMonth,depositMonthName,depositDay,depositDayOfWeek,depositDayOfWeekName,")
           .append("amount,source,notes,createdAt\n");

        for (Earning e : earnings) {
            int year = e.getDepositDate() != null ? e.getDepositDate().getYear() : 0;
            int month = e.getDepositDate() != null ? e.getDepositDate().getMonthValue() : 0;
            String monthName = e.getDepositDate() != null
                    ? e.getDepositDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) : "";
            int day = e.getDepositDate() != null ? e.getDepositDate().getDayOfMonth() : 0;
            int dayOfWeek = e.getDepositDate() != null ? e.getDepositDate().getDayOfWeek().getValue() : 0;
            String dayOfWeekName = e.getDepositDate() != null
                    ? e.getDepositDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH) : "";

            csv.append(e.getRefId()).append(",")
               .append(escapeCsv(e.getUuid())).append(",")
               .append(escapeCsv(e.getUsername())).append(",")
               .append(e.getDepositDate() != null ? e.getDepositDate() : "").append(",")
               .append(year).append(",")
               .append(month).append(",")
               .append(monthName).append(",")
               .append(day).append(",")
               .append(dayOfWeek).append(",")
               .append(dayOfWeekName).append(",")
               .append(e.getAmount()).append(",")
               .append(escapeCsv(e.getSource())).append(",")
               .append(escapeCsv(e.getNotes())).append(",")
               .append(e.getCreatedAt() != null ? e.getCreatedAt() : "")
               .append("\n");
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportDeposits(ExportFilter filter) {
        log.info("Exporting deposits with filter: {}", filter);

        Specification<Deposit> spec = Specification.unrestricted();
        if (StringUtils.isNotEmpty(filter.getOwner())) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("username"), filter.getOwner()));
        }
        if (StringUtils.isNotEmpty(filter.getHouseholdRefId())) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.join("household").get("refId"), Long.parseLong(filter.getHouseholdRefId())));
        }
        if (filter.getDateFrom() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("depositDate"), filter.getDateFrom()));
        }
        if (filter.getDateTo() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("depositDate"), filter.getDateTo()));
        }

        List<Deposit> deposits = depositRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "depositDate"));
        log.info("Found {} deposits to export", deposits.size());

        StringBuilder csv = new StringBuilder();
        csv.append("refId,uuid,username,householdRefId,householdName,householdCurrency,")
           .append("depositDate,depositYear,depositMonth,depositMonthName,depositDay,depositDayOfWeek,depositDayOfWeekName,")
           .append("amount,source,notes,createdAt\n");

        for (Deposit d : deposits) {
            String householdRefId = d.getHousehold() != null ? String.valueOf(d.getHousehold().getRefId()) : "";
            String householdName  = d.getHousehold() != null ? escapeCsv(d.getHousehold().getName()) : "";
            String currency       = d.getHousehold() != null && d.getHousehold().getCurrency() != null
                    ? d.getHousehold().getCurrency().name() : "";

            int year = d.getDepositDate() != null ? d.getDepositDate().getYear() : 0;
            int month = d.getDepositDate() != null ? d.getDepositDate().getMonthValue() : 0;
            String monthName = d.getDepositDate() != null
                    ? d.getDepositDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) : "";
            int day = d.getDepositDate() != null ? d.getDepositDate().getDayOfMonth() : 0;
            int dayOfWeek = d.getDepositDate() != null ? d.getDepositDate().getDayOfWeek().getValue() : 0;
            String dayOfWeekName = d.getDepositDate() != null
                    ? d.getDepositDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH) : "";

            csv.append(d.getRefId()).append(",")
               .append(escapeCsv(d.getUuid())).append(",")
               .append(escapeCsv(d.getUsername())).append(",")
               .append(householdRefId).append(",")
               .append(householdName).append(",")
               .append(currency).append(",")
               .append(d.getDepositDate() != null ? d.getDepositDate() : "").append(",")
               .append(year).append(",")
               .append(month).append(",")
               .append(monthName).append(",")
               .append(day).append(",")
               .append(dayOfWeek).append(",")
               .append(dayOfWeekName).append(",")
               .append(d.getAmount()).append(",")
               .append(escapeCsv(d.getSource())).append(",")
               .append(escapeCsv(d.getNotes())).append(",")
               .append(d.getCreatedAt() != null ? d.getCreatedAt() : "")
               .append("\n");
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escapeCsv(Object value) {
        if (value == null) return "";
        String s = value.toString().trim();
        if (s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r")) {
            s = "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}
