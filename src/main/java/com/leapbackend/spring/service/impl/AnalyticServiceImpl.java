package com.leapbackend.spring.service.impl;

import com.leapbackend.spring.models.*;
import com.leapbackend.spring.repository.*;
import com.leapbackend.spring.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AnalyticServiceImpl implements AnalyticsService {

    @Autowired
    private AnalyticsRepository analyticsRepository;

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private ManagerDetailRepository managerDetailRepository;

    @Autowired
    private PurchaseHistoryRepository purchaseHistoryRepository;

    @Override
    public Analytics createAnalytics(Long managerId) {
//        Promotion promotion = promotionRepository.findById(promotionId).orElseThrow();
        ManagerDetail manager = managerDetailRepository.findById(managerId).orElseThrow();

        Analytics analytics = new Analytics();
//        analytics.setPromotion(promotion);
        analytics.setManager(manager);

        Double pre_Revenue = promotionRepository.findTotalRevenueByManagerBeforePromotion(managerId);
        double preRevenue = (pre_Revenue != null) ? pre_Revenue : 0.0;

        Double post_Revenue = promotionRepository.findTotalDiscountedRevenueByManager(managerId);
        double postRevenue = (post_Revenue != null) ? post_Revenue : 0.0;

        double convRate = calculateConvRate(managerId);


        analytics.setPreRevenue(preRevenue);
        analytics.setPostRevenue(postRevenue);
        analytics.setConvRate(convRate);

        int preInteractions = 0;
        Integer preInteractionsResult = purchaseHistoryRepository.countInteractionsBeforePromotion(managerId);
        if (preInteractionsResult != null) {
            preInteractions = preInteractionsResult;
        }

        int postInteractions = 0;
        Integer postInteractionsResult = promotionRepository.countBoughtCustomersByManager(managerId);
        if (postInteractionsResult != null) {
            postInteractions = postInteractionsResult;
        }


        analytics.setPreInteractions(preInteractions);
        analytics.setPostInteractions(postInteractions);

        analytics.setLastUpdated(LocalDateTime.now());

        return analyticsRepository.save(analytics);
    }

    @Override
    public Analytics getAnalyticsById(Long id) {

        Optional<Analytics> analyticsOptional=analyticsRepository.findById(id);
        if(analyticsOptional.isEmpty())
        {
            return null;
        }
        return analyticsOptional.get();
    }

    @Override
    public List<Analytics> getAllAnalytics() {
        return analyticsRepository.findAll();
    }

    @Override
    public Analytics updateAnalytics(Long id) {
        Analytics existingAnalytics = analyticsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Analytics not found with id " + id));

        Long managerId = existingAnalytics.getManager().getId();

        Double pre_Revenue = promotionRepository.findTotalRevenueByManagerBeforePromotion(managerId);
        double preRevenue = (pre_Revenue != null) ? pre_Revenue : 0.0;

        Double post_Revenue = promotionRepository.findTotalDiscountedRevenueByManager(managerId);
        double postRevenue = (post_Revenue != null) ? post_Revenue : 0.0;

        double convRate = calculateConvRate(managerId);

        existingAnalytics.setPreRevenue(preRevenue);
        existingAnalytics.setPostRevenue(postRevenue);
        existingAnalytics.setConvRate(convRate);

        int preInteractions = 0;
        Integer preInteractionsResult = purchaseHistoryRepository.countInteractionsBeforePromotion(managerId);
        if (preInteractionsResult != null) {
            preInteractions = preInteractionsResult;
        }

        int postInteractions = 0;
        Integer postInteractionsResult = promotionRepository.countBoughtCustomersByManager(managerId);
        if (postInteractionsResult != null) {
            postInteractions = postInteractionsResult;
        }

        existingAnalytics.setPreInteractions(preInteractions);
        existingAnalytics.setPostInteractions(postInteractions);

        existingAnalytics.setLastUpdated(LocalDateTime.now());

        return analyticsRepository.save(existingAnalytics);
    }

    private double calculateConvRate(Long managerId) {
        int interestedCount = promotionRepository.countInterestedCustomersByManager(managerId);
        int boughtCount = promotionRepository.countBoughtCustomersByManager(managerId);

        if (boughtCount >= interestedCount) {
            return 100.0;
        } else {
            return (boughtCount / (double) interestedCount) * 100;
        }
    }

}
