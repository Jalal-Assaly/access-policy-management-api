package org.example.accesspolicymanagementapi.mapper;

import org.example.accesspolicymanagementapi.documents.AccessPolicy;
import org.example.accesspolicymanagementapi.models.AccessPolicyModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = {UserAttributesMapper.class, AccessPointAttributesMapper.class})
public interface AccessPolicyMapper {

    @Mapping(source = "userAttributesSet", target="userAttributesSetModel")
    @Mapping(source = "accessPointAttributes", target="accessPointAttributesModel")
    AccessPolicyModel toModel(final AccessPolicy accessPolicy);

    @Mapping(source = "userAttributesSetModel", target="userAttributesSet")
    @Mapping(source = "accessPointAttributesModel", target="accessPointAttributes")
    AccessPolicy toDocument(final AccessPolicyModel accessPolicyModel);
}
