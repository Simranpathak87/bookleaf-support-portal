package bookleaf.supportportal.dto;



public record LoginResponse(String token, String userId, String name, String role) {}