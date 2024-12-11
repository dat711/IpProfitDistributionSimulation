package com.LegalEntitiesManagement.v1.unitTests.ServicesTests;

import com.LegalEntitiesManagement.v1.Entities.model.Role;
import com.LegalEntitiesManagement.v1.Entities.model.StakeHolder;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.StakeHolderLeaf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StakeholderEqualityTest {
    @Nested
    @DisplayName("StakeHolder Equality Tests")
    class StakeHolderTests {
        private StakeHolder stakeHolder1;
        private StakeHolder stakeHolder2;
        private Role role;

        @BeforeEach
        void setUp() {
            role = new Role();
            role.setId(1L);
            role.setName("Test Role");
            role.setPriority(1);

            stakeHolder1 = new StakeHolder("Test Stakeholder", role);
            stakeHolder1.setId(1L);

            stakeHolder2 = new StakeHolder("Test Stakeholder", role);
            stakeHolder2.setId(1L);
        }

        @Test
        @DisplayName("Equal stakeholders should have same hashcode")
        void equalStakeholdersShouldHaveSameHashcode() {
            assertEquals(stakeHolder1.hashCode(), stakeHolder2.hashCode());
        }

        @Test
        @DisplayName("Stakeholder should be equal to itself")
        void stakeholderShouldBeEqualToItself() {
            assertEquals(stakeHolder1, stakeHolder1);
        }

        @Test
        @DisplayName("Stakeholders with same ID should be equal")
        void stakeholdersWithSameIdShouldBeEqual() {
            assertEquals(stakeHolder1, stakeHolder2);
        }

        @Test
        @DisplayName("Stakeholders with different IDs should not be equal")
        void stakeholdersWithDifferentIdsShouldNotBeEqual() {
            stakeHolder2.setId(2L);
            assertNotEquals(stakeHolder1, stakeHolder2);
        }

        @Test
        @DisplayName("Stakeholder should not be equal to null")
        void stakeholderShouldNotBeEqualToNull() {
            assertNotEquals(stakeHolder1, null);
        }

        @Test
        @DisplayName("Stakeholder should not be equal to other type")
        void stakeholderShouldNotBeEqualToOtherType() {
            Object otherObject = new Object();
            assertNotEquals(stakeHolder1, otherObject);
        }

        @Test
        @DisplayName("Different names should not affect equality")
        void differentNamesShouldNotAffectEquality() {
            stakeHolder2.setName("Different Name");
            assertEquals(stakeHolder1, stakeHolder2);
        }

        @Test
        @DisplayName("Different roles should not affect equality")
        void differentRolesShouldNotAffectEquality() {
            Role differentRole = new Role();
            differentRole.setId(2L);
            differentRole.setName("Different Role");
            differentRole.setPriority(2);

            stakeHolder2.setRole(differentRole);
            assertEquals(stakeHolder1, stakeHolder2);
        }
    }

    @Nested
    @DisplayName("StakeHolderLeaf Equality Tests")
    class StakeHolderLeafTests {
        private StakeHolderLeaf leaf1;
        private StakeHolderLeaf leaf2;
        private StakeHolder stakeHolder;
        private Role role;

        @BeforeEach
        void setUp() {
            role = new Role();
            role.setId(1L);
            role.setName("Test Role");
            role.setPriority(1);

            stakeHolder = new StakeHolder("Test Stakeholder", role);
            stakeHolder.setId(1L);

            leaf1 = new StakeHolderLeaf(stakeHolder);
            leaf1.setId(1L);

            leaf2 = new StakeHolderLeaf(stakeHolder);
            leaf2.setId(1L);
        }

        @Test
        @DisplayName("Equal leaves should have same hashcode")
        void equalLeavesShouldHaveSameHashcode() {
            assertEquals(leaf1.hashCode(), leaf2.hashCode());
        }

        @Test
        @DisplayName("Leaf should be equal to itself")
        void leafShouldBeEqualToItself() {
            assertEquals(leaf1, leaf1);
        }

        @Test
        @DisplayName("Leaves with same IDs and stakeholders should be equal")
        void leavesWithSameIdsAndStakeholdersShouldBeEqual() {
            assertEquals(leaf1, leaf2);
        }

        @Test
        @DisplayName("Leaves with different IDs should not be equal")
        void leavesWithDifferentIdsShouldNotBeEqual() {
            leaf2.setId(2L);
            assertNotEquals(leaf1, leaf2);
        }

        @Test
        @DisplayName("Leaves with different stakeholders should not be equal")
        void leavesWithDifferentStakeholdersShouldNotBeEqual() {
            StakeHolder differentStakeHolder = new StakeHolder("Different Stakeholder", role);
            differentStakeHolder.setId(2L);
            leaf2.setStakeHolder(differentStakeHolder);
            assertNotEquals(leaf1, leaf2);
        }

        @Test
        @DisplayName("Leaf should not be equal to null")
        void leafShouldNotBeEqualToNull() {
            assertNotEquals(leaf1, null);
        }

        @Test
        @DisplayName("Leaf should not be equal to other type")
        void leafShouldNotBeEqualToOtherType() {
            Object otherObject = new Object();
            assertNotEquals(leaf1, otherObject);
        }

        @Test
        @DisplayName("Same node ID different stakeholder ID should not be equal")
        void sameNodeIdDifferentStakeholderIdShouldNotBeEqual() {
            StakeHolder differentStakeHolder = new StakeHolder("Different Stakeholder", role);
            differentStakeHolder.setId(2L);
            leaf2.setStakeHolder(differentStakeHolder);
            assertNotEquals(leaf1, leaf2);
        }
    }
}
