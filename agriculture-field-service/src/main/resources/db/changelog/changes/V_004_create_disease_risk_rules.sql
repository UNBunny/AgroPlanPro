-- =============================================
-- V_004: Таблица правил оценки рисков болезней
-- =============================================

CREATE TABLE IF NOT EXISTS disease_risk_rules (
    id                      BIGSERIAL PRIMARY KEY,
    disease_name            VARCHAR(255) NOT NULL,
    disease_type            VARCHAR(50),
    affected_crops          VARCHAR(500) NOT NULL,
    risk_level              VARCHAR(20) NOT NULL,
    risk_weight             DOUBLE PRECISION NOT NULL DEFAULT 0.5,

    -- Условия по температуре
    temp_min_threshold      DOUBLE PRECISION,
    temp_max_threshold      DOUBLE PRECISION,

    -- Условия по осадкам (за 7 дней)
    precip_min7d            DOUBLE PRECISION,
    precip_max7d            DOUBLE PRECISION,

    -- Условия по влажности
    humidity_min_threshold  DOUBLE PRECISION,

    -- Условия по ГТК
    gtk_min                 DOUBLE PRECISION,
    gtk_max                 DOUBLE PRECISION,

    -- Условия по тепловому стрессу
    heat_stress_days_min    INTEGER,

    -- Условия по засухе
    dry_period_days_min     INTEGER,

    -- Активный сезон (месяцы через запятую)
    active_season           VARCHAR(100),

    -- Описания и рекомендации
    rule_description        VARCHAR(500),
    prevention_advice       VARCHAR(1000),
    treatment_advice        VARCHAR(1000),
    urgency_days            INTEGER,

    is_active               BOOLEAN NOT NULL DEFAULT TRUE
);

-- =============================================
-- Начальные правила для оценки рисков болезней
-- =============================================

-- 1. Бурая ржавчина пшеницы (Puccinia triticina)
INSERT INTO disease_risk_rules (
    disease_name, disease_type, affected_crops, risk_level, risk_weight,
    temp_min_threshold, temp_max_threshold, precip_min7d,
    gtk_min, active_season,
    rule_description, prevention_advice, treatment_advice, urgency_days
) VALUES (
    'Бурая ржавчина', 'FUNGAL', 'пшеница,пшеница озимая,пшеница яровая',
    'HIGH', 0.85,
    15.0, 22.0, 10.0,
    1.0, '5,6,7,8',
    'T=15-22°C, осадки >10мм/нед, ГТК >1.0 — идеальные условия для бурой ржавчины',
    'Профилактическая обработка фунгицидом (триазолы) при первых признаках. Используйте устойчивые сорта.',
    'Обработка Пропиконазол 250 г/л или Тебуконазол 250 г/л. Повторная обработка через 14 дней.',
    5
);

-- 2. Септориоз (Septoria tritici)
INSERT INTO disease_risk_rules (
    disease_name, disease_type, affected_crops, risk_level, risk_weight,
    temp_min_threshold, temp_max_threshold, precip_min7d,
    gtk_min, active_season,
    rule_description, prevention_advice, treatment_advice, urgency_days
) VALUES (
    'Септориоз', 'FUNGAL', 'пшеница,пшеница озимая,пшеница яровая,ячмень',
    'HIGH', 0.80,
    15.0, 20.0, 30.0,
    1.5, '5,6,7',
    'T=15-20°C, обильные осадки >30мм/нед, высокий ГТК — благоприятные условия для септориоза',
    'Протравливание семян, соблюдение севооборота. Обработка фунгицидами в фазу кущения.',
    'Применение стробилуринов (Азоксистробин) или комбинированных препаратов (Амистар Экстра).',
    7
);

-- 3. Фузариоз колоса (Fusarium graminearum)
INSERT INTO disease_risk_rules (
    disease_name, disease_type, affected_crops, risk_level, risk_weight,
    temp_min_threshold, temp_max_threshold, precip_min7d,
    gtk_min, active_season,
    rule_description, prevention_advice, treatment_advice, urgency_days
) VALUES (
    'Фузариоз колоса', 'FUNGAL', 'пшеница,пшеница озимая,пшеница яровая,ячмень',
    'CRITICAL', 0.90,
    15.0, 25.0, 15.0,
    1.2, '6,7',
    'T=15-25°C, осадки в период цветения >15мм — высокий риск фузариоза колоса',
    'Протравливание семян фунгицидами. Избегайте посева пшеницы после кукурузы. Используйте устойчивые сорта.',
    'Обработка Тебуконазол + Протиоконазол в фазу цветения. Критично обработать в первые 3 дня!',
    3
);

-- 4. Мучнистая роса (Blumeria graminis)
INSERT INTO disease_risk_rules (
    disease_name, disease_type, affected_crops, risk_level, risk_weight,
    temp_min_threshold, temp_max_threshold, precip_min7d,
    humidity_min_threshold, active_season,
    rule_description, prevention_advice, treatment_advice, urgency_days
) VALUES (
    'Мучнистая роса', 'FUNGAL', 'пшеница,ячмень,овёс',
    'MEDIUM', 0.65,
    12.0, 20.0, 5.0,
    80.0, '5,6,7',
    'T=12-20°C, умеренные осадки, высокая влажность — условия для мучнистой росы',
    'Использование устойчивых сортов. Избегайте загущенных посевов. Азотные удобрения — умеренно.',
    'Обработка серосодержащими фунгицидами или триазолами.',
    10
);

-- 5. Пероноспороз подсолнечника (Plasmopara halstedii)
INSERT INTO disease_risk_rules (
    disease_name, disease_type, affected_crops, risk_level, risk_weight,
    temp_min_threshold, temp_max_threshold, precip_min7d,
    gtk_min, active_season,
    rule_description, prevention_advice, treatment_advice, urgency_days
) VALUES (
    'Пероноспороз', 'FUNGAL', 'подсолнечник',
    'HIGH', 0.75,
    10.0, 18.0, 20.0,
    1.5, '5,6,7',
    'T=10-18°C, обильные осадки, высокий ГТК — благоприятные условия для пероноспороза',
    'Протравливание семян Мефеноксам. Севооборот: не сеять подсолнечник чаще 1 раза в 4 года.',
    'Системные фунгициды на основе Металаксил-М. Удалить поражённые растения.',
    5
);

-- 6. Фитофтороз (Phytophthora infestans) — картофель
INSERT INTO disease_risk_rules (
    disease_name, disease_type, affected_crops, risk_level, risk_weight,
    temp_min_threshold, temp_max_threshold, precip_min7d,
    gtk_min, active_season,
    rule_description, prevention_advice, treatment_advice, urgency_days
) VALUES (
    'Фитофтороз', 'FUNGAL', 'картофель',
    'CRITICAL', 0.90,
    12.0, 20.0, 20.0,
    1.5, '6,7,8',
    'T=12-20°C, высокая влажность и осадки — экстремальный риск фитофтороза',
    'Профилактическое опрыскивание контактными фунгицидами (Манкоцеб) до появления признаков.',
    'Системные фунгициды (Ридомил Голд). Обработка каждые 7-10 дней. Уничтожение ботвы перед уборкой.',
    3
);

-- 7. Ржавчина подсолнечника (Puccinia helianthi)
INSERT INTO disease_risk_rules (
    disease_name, disease_type, affected_crops, risk_level, risk_weight,
    temp_min_threshold, temp_max_threshold, precip_min7d,
    active_season,
    rule_description, prevention_advice, treatment_advice, urgency_days
) VALUES (
    'Ржавчина подсолнечника', 'FUNGAL', 'подсолнечник',
    'MEDIUM', 0.60,
    18.0, 28.0, 8.0,
    '6,7,8',
    'T=18-28°C, умеренные осадки — условия для развития ржавчины подсолнечника',
    'Использование устойчивых гибридов. Севооборот 3-4 года.',
    'Обработка триазоловыми фунгицидами при первых признаках поражения.',
    7
);

-- 8. Засуха — абиотический стресс (все культуры)
INSERT INTO disease_risk_rules (
    disease_name, disease_type, affected_crops, risk_level, risk_weight,
    gtk_max, dry_period_days_min,
    active_season,
    rule_description, prevention_advice, treatment_advice, urgency_days
) VALUES (
    'Засуха (абиотический стресс)', 'ABIOTIC', 'пшеница,ячмень,кукуруза,подсолнечник,соя,рапс,картофель',
    'HIGH', 0.80,
    0.5, 10,
    '5,6,7,8',
    'ГТК < 0.5, сухой период > 10 дней — высокий риск засухи',
    'Организуйте орошение. Мульчирование для сохранения влаги. Внесите калийные удобрения для стрессоустойчивости.',
    'Снизьте норму азотных удобрений. При сильной засухе — антистрессовые препараты (Мегафол, Аминокат).',
    3
);

-- 9. Корневые гнили (Bipolaris sorokiniana) — ячмень
INSERT INTO disease_risk_rules (
    disease_name, disease_type, affected_crops, risk_level, risk_weight,
    temp_min_threshold, temp_max_threshold, precip_min7d,
    gtk_min, active_season,
    rule_description, prevention_advice, treatment_advice, urgency_days
) VALUES (
    'Корневые гнили', 'FUNGAL', 'ячмень,пшеница,овёс',
    'MEDIUM', 0.65,
    10.0, 22.0, 20.0,
    1.3, '4,5,6',
    'T=10-22°C, избыток влаги в почве — благоприятные условия для корневых гнилей',
    'Протравливание семян (Тирам, Карбендазим). Соблюдайте севооборот — не сейте зерновые после зерновых.',
    'Обработка фунгицидами по вегетации. Применение биопрепаратов (Триходерма).',
    7
);

-- 10. Тепловой стресс — абиотический (все культуры)
INSERT INTO disease_risk_rules (
    disease_name, disease_type, affected_crops, risk_level, risk_weight,
    heat_stress_days_min,
    active_season,
    rule_description, prevention_advice, treatment_advice, urgency_days
) VALUES (
    'Тепловой стресс (абиотический)', 'ABIOTIC', 'пшеница,ячмень,кукуруза,подсолнечник,соя,рапс',
    'HIGH', 0.75,
    5,
    '6,7,8',
    '5+ дней с температурой >30°C — высокий риск теплового стресса',
    'Обеспечьте полив в утренние часы. Применяйте антистрессовые препараты.',
    'Внекорневая подкормка кальцием и бором. Антистрессовые препараты (Мегафол, салициловая кислота).',
    2
);

