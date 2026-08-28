package br.com.uktech.tests.cms.backend.config;

/**
 * Copyright (C) 2025 Uhlig & Korovsky Tecnologia Ltda - All Rights Reserved
 * <p>
 * This source code is protected under international copyright law.  All rights
 * reserved and protected by the copyright holders.
 * This file is confidential and only available to authorized individuals with the
 * permission of the copyright holders.  If you encounter this file and do not have
 * permission, please contact the copyright holders and delete this file.
 */

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

/**
 * @created at 27/08/2026
 * @author Carlos Alberto Cipriano Korovsky <carlos.korovsky@uktech.com.br>
 */
@Configuration
@EnableR2dbcRepositories
@EnableR2dbcAuditing
public class R2dbcRepositoriesConfig {
}
