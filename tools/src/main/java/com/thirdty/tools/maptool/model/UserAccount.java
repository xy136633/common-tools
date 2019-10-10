package com.thirdty.tools.maptool.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserAccount {

	private String userId;
	private String userName;
	private Long money;
}
