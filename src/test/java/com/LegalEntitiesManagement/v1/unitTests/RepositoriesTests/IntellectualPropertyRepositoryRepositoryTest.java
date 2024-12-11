package com.LegalEntitiesManagement.v1.unitTests.RepositoriesTests;


import com.LegalEntitiesManagement.v1.Entities.exceptions.IpNotFoundException;
import com.LegalEntitiesManagement.v1.Entities.model.IntellectualProperty;
import com.LegalEntitiesManagement.v1.Entities.repositories.IntellectualPropertyRepository;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IntellectualPropertyRepositoryRepositoryTest extends BaseRepositoryTestProperties {
    @Autowired
    private IntellectualPropertyRepository intellectualPropertyRepository;

    @Test
    @Order(1)
    void TestIntellectualPropertyInsertingFunction(){
        IntellectualProperty sundayMorning = new IntellectualProperty("Sunday Morning",
                "Pop Song of Maroon 5");
        IntellectualProperty savedIp = this.intellectualPropertyRepository.save(sundayMorning);
        assertEquals(savedIp.getId(),1L);
    }

    @Test
    @Order(2)
    void TestIntellectualPropertyExistFunction(){
        IntellectualProperty sundayMorning = new IntellectualProperty("Sunday Morning",
                "Pop Song of Maroon 5");
        IntellectualProperty savedIp = this.intellectualPropertyRepository.save(sundayMorning);
        assertTrue(this.intellectualPropertyRepository.existsById(2L));
    }

    @Test
    @Order(3)
    void TestIntellectualPropertyGetFunction(){
        IntellectualProperty sundayMorning = new IntellectualProperty("Sunday Morning",
                "Pop Song of Maroon 5");
        this.intellectualPropertyRepository.save(sundayMorning);
        IntellectualProperty savedIp = this.intellectualPropertyRepository.findById(3L).orElseThrow(() -> new IpNotFoundException(3L));
        assertEquals(savedIp.getName(),"Sunday Morning");
        assertEquals(savedIp.getDescription(),"Pop Song of Maroon 5");
    }
}
