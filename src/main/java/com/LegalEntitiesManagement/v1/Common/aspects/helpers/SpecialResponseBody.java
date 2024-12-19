package com.LegalEntitiesManagement.v1.Common.aspects.helpers;

import com.LegalEntitiesManagement.v1.Entities.dto.ContractCompositionDto;
import com.LegalEntitiesManagement.v1.Entities.dto.IpBasedContractCompositionDto;
import com.LegalEntitiesManagement.v1.Entities.dto.IpBasedContractDto;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class SpecialResponseBody {
    public static HashMap<String,String> noContentResponse(String fieldName){
        String message = String.format("There is no record of object %s", fieldName);
        HashMap<String,String> noContentBodyResponse = new HashMap<>();
        noContentBodyResponse.put("message",message);
        return noContentBodyResponse;
    }

    public static HashMap<String,String> deleteObject(String object, long Id){
        String message = String.format("The %s with id %s is removed", object, Id);
        HashMap<String,String> deletedResponse = new HashMap<>();
        deletedResponse.put("message", message);
        return deletedResponse;
    }

    public static Map<String, Object> addLink(Class<?> clazz, Object item, String linkTemplate){
        List<Field> fields = List.of(clazz.getDeclaredFields());
        Long id = fields.stream().peek(field -> field.setAccessible(true))
                .filter(field -> field.getName().equals("id"))
                .mapToLong(field -> {
                    try {
                        return (Long) field.get(item);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }).findFirst().orElseThrow();
        fields.forEach(field -> field.setAccessible(false));
        String link = String.format( "%s/%s",linkTemplate, id);
        HashMap<String, Object> addedLink = new HashMap<>();
        addedLink.put("Link", link);
        addedLink.put("data", clazz.cast(item));
        return addedLink;
    }

    public static Collection<Map<String, Object>> addLinks(Class<?> clazz, Collection<Object> items, String linkTemplate){
        return items.stream().map(
                item -> SpecialResponseBody.addLink(clazz, item, linkTemplate)
        ).collect(Collectors.toSet());
    }

    public static SuccessResponse<Object> getSuccessResponses(Class<?> clazz, Object item, String linkTemplate, String message){
        Map<String, Object> data = addLink(clazz, item, linkTemplate);
        return SuccessResponse.successResponse(data,message);
    }

    // for contract

    public static  Map<String, Object> addLinkToContractComposition(ContractCompositionDto dto, String linkTemplate){
        Long id = dto.getContractDetail().getId();
        String link = String.format( "%s/%s",linkTemplate, id);
        HashMap<String, Object> addedLink = new HashMap<>();
        addedLink.put("Link", link);
        addedLink.put("data", dto);
        return addedLink;
    }

    public static Map<String, Object> addLinkToIpBasedContractComposition(IpBasedContractCompositionDto dto, String linkTemplate){
        Long id = dto.getContractDetail().getId();
        String link = String.format( "%s/%s",linkTemplate, id);
        HashMap<String, Object> addedLink = new HashMap<>();
        addedLink.put("Link", link);
        addedLink.put("data", dto);
        return addedLink;
    }

    public static List<Map<String, Object>> addLinksToListContractComposition(List<ContractCompositionDto> dtos, String linkTemplate){
        return dtos.stream().map(dto -> SpecialResponseBody.addLinkToContractComposition(dto, linkTemplate)).toList();
    }

    public static List<Map<String, Object>> addLinksToListIpBasedContractComposition(List<IpBasedContractCompositionDto> dtos, String linkTemplate){
        return dtos.stream().map(dto -> SpecialResponseBody.addLinkToIpBasedContractComposition(dto, linkTemplate)).toList();
    }

    public static SuccessResponse<Object> getSuccessResponseContract( ContractCompositionDto dto , String linkTemplate, String message){
        Map<String, Object> data = addLinkToContractComposition(dto, linkTemplate);
        return SuccessResponse.successResponse(data,message);
    }

    public static SuccessResponse<Object> getSuccessResponseIpBasedContract(IpBasedContractCompositionDto dto , String linkTemplate, String message){
        Map<String, Object> data = addLinkToIpBasedContractComposition(dto, linkTemplate);
        return SuccessResponse.successResponse(data,message);
    }

    public static SuccessResponse<Object> getSuccessResponseListIpBasedContract(List<IpBasedContractCompositionDto> dtos, String linkTemplate, String message){
        return SuccessResponse.successResponse(
                addLinksToListIpBasedContractComposition(dtos, linkTemplate),
                message
        );
    }
}