package org.example.accesspolicymanagementapi.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.accesspolicymanagementapi.documents.AccessPolicy;
import org.example.accesspolicymanagementapi.documents.DatabaseSequence;
import org.example.accesspolicymanagementapi.mapper.AccessPolicyMapper;
import org.example.accesspolicymanagementapi.models.AccessPolicyModel;
import org.example.accesspolicymanagementapi.repo.AccessPolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.schema.MongoJsonSchema;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Validated
@Service
@Transactional
@RequiredArgsConstructor
public class AccessPolicyService {

    private final AccessPolicyMapper accessPolicyMapper;
    private final AccessPolicyRepository accessPolicyRepository;

    @Autowired
    private MongoOperations mongoOperations;

    public List<AccessPolicyModel> getAllAccessPolicies(){

        return accessPolicyRepository.findAll()
                .stream()
                .map(accessPolicyMapper::toModel)
                .collect(Collectors.toList());
    }

    public void createNewAccessPolicy(@Valid AccessPolicyModel accessPolicyModel){

        AccessPolicyModel tempAccessPolicyModel = new AccessPolicyModel();
        tempAccessPolicyModel.setId(generateSequence(AccessPolicy.SEQUENCE_NAME));
        tempAccessPolicyModel.setOccupancyLevel(accessPolicyModel.getOccupancyLevel());
        tempAccessPolicyModel.setLocation(accessPolicyModel.getLocation());
        tempAccessPolicyModel.setAuthorizedUserAttributes(accessPolicyModel.getAuthorizedUserAttributes());
        accessPolicyRepository.insert(accessPolicyMapper.toDocument(tempAccessPolicyModel));
    }

    public void updateAccessPolicy(@Valid AccessPolicyModel accessPolicyModel, String accessPolicyId){
        accessPolicyMapper.toModel(accessPolicyRepository
                .findById(accessPolicyId)
                .orElseThrow(() -> new EntityNotFoundException("The Access Policy with ID : (" + accessPolicyId + ") does not exist")));

        accessPolicyRepository.save(accessPolicyMapper.toDocument(accessPolicyModel));

    }
    public void deleteAccessPolicy(String accessPolicyId) {
        accessPolicyMapper.toModel(accessPolicyRepository
                .findById(accessPolicyId)
                .orElseThrow(() -> new EntityNotFoundException("The Access Policy with ID : (" + accessPolicyId + ") does not exist")));

        accessPolicyRepository.deleteById(accessPolicyId);
    }

    public AccessPolicyModel findAccessPolicy(String location) {
        return accessPolicyMapper.toModel(accessPolicyRepository
                .findAccessPolicyByLocation(location)
                .orElseThrow(() -> new EntityNotFoundException("The Access Policy with location : (" + location + ") does not exist")));
    }

    public String generateSequence(String seqName) {
        DatabaseSequence counter = mongoOperations.findAndModify(query(where("_id").is(seqName)),
                new Update().inc("seq",1), options().returnNew(true).upsert(true),
                DatabaseSequence.class);
        return String.valueOf(!Objects.isNull(counter) ? counter.getSeq() : 1);
    }




}
