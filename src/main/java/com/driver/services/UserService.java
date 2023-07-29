package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository

        Optional<User> optionalUser = userRepository.findById(userId);
        User user = optionalUser.get();

        if (user.getSubscription()==null) return 0;
        SubscriptionType subscriptionType = user.getSubscription().getSubscriptionType();
        List<WebSeries> webSeriesList = webSeriesRepository.findAll();
        int cnt=0;

        if(subscriptionType.equals(SubscriptionType.ELITE)){
            for (WebSeries webSeries:webSeriesList) {
                if (webSeries.getAgeLimit()<=user.getAge()) cnt++;
            }
        } else if (subscriptionType.equals(SubscriptionType.PRO)) {
            for (WebSeries webSeries:webSeriesList) {
                if (!webSeries.getSubscriptionType().equals(SubscriptionType.ELITE) && webSeries.getAgeLimit()<=user.getAge()) cnt++;
            }
        }else{
            for (WebSeries webSeries:webSeriesList) {
                if (webSeries.getSubscriptionType().equals(SubscriptionType.BASIC) && webSeries.getAgeLimit()<=user.getAge()) cnt++;
            }
        }

        return cnt;
    }


}
