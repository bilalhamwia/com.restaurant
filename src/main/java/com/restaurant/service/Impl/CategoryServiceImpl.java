package com.restaurant.service.Impl;

import com.google.common.base.Strings;
import com.restaurant.JWT.JwtFilter;
import com.restaurant.constents.RestaurantConstants;
import com.restaurant.module.Category;
import com.restaurant.repository.CategoryRepository;
import com.restaurant.service.CategoryService;
import com.restaurant.utils.RestaurantUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                if (validateCategoryMap(requestMap, false)) {
                    categoryRepository.save(getCategoryFromMap(requestMap, false));
                    return RestaurantUtils.getResponseEntity("Category Added Successfully",HttpStatus.OK);
                }
            }
            else {
                return RestaurantUtils.getResponseEntity(RestaurantConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateCategoryMap(Map<String, String> requestMap, boolean validateId) {
        if (requestMap.containsKey("name")) {
            if (requestMap.containsKey("id") && validateId) {
                return true;
            } else if (!validateId) {
                return true;
            }
        }
        return false;
    }

    private Category getCategoryFromMap(Map<String, String> requestMap, Boolean isAdd) {
        Category category = new Category();
        if (isAdd) {
            category.setId(Integer.parseInt(requestMap.get("id")));
        }
        category.setName(requestMap.get("name"));
        return category;
    }

    @Override
    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
        try {
            if (!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")) {
                log.info("Inside if filterValue");
                return new ResponseEntity<List<Category>>(categoryRepository.getAllCategory(), HttpStatus.OK);
            }
            return new ResponseEntity<>(categoryRepository.findAll(), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<List<Category>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
