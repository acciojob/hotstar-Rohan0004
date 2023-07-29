package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        Subscription subscription=user.getSubscription();
        if (subscription != null) return subscription.getTotalAmountPaid();

        subscription=new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        subscription.setStartSubscriptionDate(new Date());

        SubscriptionType subscriptionType=subscription.getSubscriptionType();

        if(subscriptionType.equals(SubscriptionType.ELITE)){
            subscription.setTotalAmountPaid(1000+subscriptionEntryDto.getNoOfScreensRequired()*350);
        } else if (subscriptionType.equals(SubscriptionType.PRO)) {
            subscription.setTotalAmountPaid(800+subscriptionEntryDto.getNoOfScreensRequired()*250);
        }else{
            subscription.setTotalAmountPaid(500+subscriptionEntryDto.getNoOfScreensRequired()*200);
        }

        subscription.setUser(user);
        user.setSubscription(subscription);

        userRepository.save(user);
        return subscription.getTotalAmountPaid();
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user = userRepository.findById(userId).get();
        Subscription subscription = user.getSubscription();
        if (subscription==null) return -1;

        if (subscription.getSubscriptionType().equals(SubscriptionType.ELITE)) throw new Exception("Already the best Subscription");

        if (subscription.getSubscriptionType().equals(SubscriptionType.PRO)){
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            int oldAmount=subscription.getTotalAmountPaid();
            subscription.setTotalAmountPaid(1000+subscription.getNoOfScreensSubscribed()*350);
            user.setSubscription(subscription);
            userRepository.save(user);
            return subscription.getTotalAmountPaid()-oldAmount;
        }
        subscription.setSubscriptionType(SubscriptionType.PRO);
        int oldAmount=subscription.getTotalAmountPaid();
        subscription.setTotalAmountPaid(800+subscription.getNoOfScreensSubscribed()*250);
        user.setSubscription(subscription);
        userRepository.save(user);
        return subscription.getTotalAmountPaid()-oldAmount;

    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        return subscriptionRepository.calculateTotalRevenue();
    }

}
