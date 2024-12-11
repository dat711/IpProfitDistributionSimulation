package com.LegalEntitiesManagement.v1.Entities.model.supportClass;

import com.LegalEntitiesManagement.v1.Entities.model.StakeHolder;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class utilClass {
    public static boolean noOverlapNonExecutorStakeHolder(Collection<Set<StakeHolder>> nonExecutorStakeHolder){
        Set<StakeHolder> allNonExecutorStakeHolder = nonExecutorStakeHolder.stream().flatMap(Set::stream)
                .collect(Collectors.toSet());
        return allNonExecutorStakeHolder.size() == nonExecutorStakeHolder.stream().mapToLong(Set::size).sum();
    }

    public static Set<StakeHolder> findOverlapping(Collection<Set<StakeHolder>> listOfSets) {
        Set<StakeHolder> seenOnce = new HashSet<>();
        Set<StakeHolder> duplicates = new HashSet<>();

        listOfSets.stream()
                .flatMap(Set::stream)
                .forEach(stakeHolder -> {
                    if (!seenOnce.add(stakeHolder)) {
                        duplicates.add(stakeHolder);
                    }
                });
        return duplicates;
    }
}
