package com.abhi.leximentor.inventory.controller.rest.drill;


public class DrillController {
//    private final DrillMetadataService drillMetadataService;
//    private final DrillChallengeService drillChallengeService;
//    private final DrillChallengeRepository drillChallengeRepository;
//    private final DrillChallengeScoreService drillChallengeScoreService;
//    private final DrillSetService drillSetService;
//    private final DrillEvaluationService drillEvaluationService;
//
//
//    @PostMapping(value = UrlConstants.Drill.DRILL_ASSIGN_CHALLENGES_TO_DRILLS, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<RestApiResponse> addChallenges(@RequestParam String drillId, @RequestParam String drillType) {
//        DrillMetadataDTO drillMetadataDTO = drillMetadataService.getByRefId(Long.valueOf(drillId));
//        drillMetadataDTO = drillChallengeService.addChallenges(drillMetadataDTO, DrillTypes.getType(drillType));
//        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, drillMetadataDTO);
//    }
//
//    @GetMapping(value = UrlConstants.Drill.DRILL_ADD, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<RestApiResponse> getAllDrills() {
//        List<DrillMetadataDTO> drillMetadataDTOList = drillMetadataService.getDrills();
//        return CollectionUtil.isNotEmpty(drillMetadataDTOList) ? ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, drillMetadataDTOList) : ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Unable to retrieve drills");
//    }
//
//    @GetMapping(value = UrlConstants.Drill.DRILL_GET_CHALLENGES_BY_DRILL_ID, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<RestApiResponse> getAllChallengesByDrillRefId(@PathVariable String drillId) {
//        List<DrillChallengeDTO> drillChallengeDTOList = drillChallengeService.getChallengesByDrillRefId(Long.parseLong(drillId));
//        return CollectionUtil.isNotEmpty(drillChallengeDTOList) ? ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, drillChallengeDTOList) : ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Unable to retrieve challenges");
//    }
//
//    @GetMapping(value = UrlConstants.Drill.DRILL_GET_CHALLENGE_SCORE_BY_CHALLENGE_ID, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<RestApiResponse> getAllChallengeScoresByChallengeId(@PathVariable String challengeId) {
//        DrillChallenge drillChallenge = drillChallengeRepository.findByRefId(Long.parseLong(challengeId));
//        List<DrillChallengeScoresDTO> drillChallengeScoresDTOS = drillChallengeScoreService.getByDrillChallengeId(drillChallenge);
//        return CollectionUtil.isNotEmpty(drillChallengeScoresDTOS) ? ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, drillChallengeScoresDTOS) : ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Unable to retrieve challenge scores");
//    }

}
