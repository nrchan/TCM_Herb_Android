package com.example.tcmherb

class HerbData {
    fun nameZH(index: Int) = herbNameChinese[if(index < 0) 0 else index]

    fun nameZHnl(index: Int) = herbNameChineseNewline[if(index < 0) 0 else index]

    fun nameEN(index: Int) = herbNameEnglish[if(index < 0) 0 else index]

    fun nameSC(index: Int) = scientificName[if(index < 0) 0 else index]

    fun taste(index: Int) = tasteAndTropism[if(index < 0) 0 else index]

    fun feature(index: Int) = feature[if(index < 0) 0 else index]

    private val herbNameChinese = arrayOf(
        "中文名稱",
        "黃苓",//黃芩?
        "熟地黃",
        "黨參",
        "甘草",
        "懷牛膝",
        "麥門冬",
        "白术",
        "野葛",
        "桑白皮",
        "桔梗",
        "山茱萸",
        "何首烏",
        "赤芍",
        "桂皮",
        "岷當歸",
        "川芎",
        "川紅花",
        "枳實",
        "紅棗",
        "細辛根",
        "連翹",
        "懷山藥",
        "款冬花",
        "乾薑",
        "袍附子",//炮附子?
        "桃仁",
        "紫苑",
        "烏藥",
        "銅陵鳳丹皮",
        "寧夏枸杞",
        "荳蔻",
        "茯苓",
        "桂枝",
        "懷菊花",
        "白參",
        "北黃耆",
        "知母",
        "生地黃",
        "骨碎補",
        "薏仁",
        "桑寄生"
    )

    private val herbNameChineseNewline = arrayOf(
        "中文名稱",
        "黃苓",//黃芩?
        "熟地黃",
        "黨參",
        "甘草",
        "懷牛膝",
        "麥門冬",
        "白术",
        "野葛",
        "桑白皮",
        "桔梗",
        "山茱萸",
        "何首烏",
        "赤芍",
        "桂皮",
        "岷當歸",
        "川芎",
        "川紅花",
        "枳實",
        "紅棗",
        "細辛根",
        "連翹",
        "懷山藥",
        "款冬花",
        "乾薑",
        "袍附子",//炮附子?
        "桃仁",
        "紫苑",
        "烏藥",
        "銅陵\n鳳丹皮",
        "寧夏\n枸杞",
        "荳蔻",
        "茯苓",
        "桂枝",
        "懷菊花",
        "白參",
        "北黃耆",
        "知母",
        "生地黃",
        "骨碎補",
        "薏仁",
        "桑寄生"
    )

    private val herbNameEnglish = arrayOf(
        "英文名稱",
        "Baikal Skullcap Root",
        "Radix Rehmanniae",
        "Asiabell Root",
        "Licorice",
        "Two-toothed Achyranthes",
        "Dwarf Lilyturf Root Tuber",
        "Largehead Atractylodes Rhizome",
        "Lobed Kudzuvine Root",
        "White Mulberry Root-bark",
        "Balloonflower Root",
        "Medical Dogwood",
        "Tuber Fleeceflower Root",
        "Red Paeoniae Trichocarpae",
        "Cassia Bark",
        "Chinese Angelica",
        "Szechwan Lovage Rhizome",
        "Safflower",
        "Immature Bitter Orange",
        "Common Jujube",
        "Manchurian Wildginger Herb",
        "Weeping Forsythia Fruit",
        "Common Yam Rhizome",
        "Common Coltsfoot Flower",
        "Dried Ginger",
        "Prepared Common Monkshood Daughter Root",
        "Peach Seed",
        "Tatarian Aster Root",
        "Combined Spicebush Root",
        "Tree Peony Bark",
        "Arbary Wolfberry",
        "Nutmeg",
        "Indian Buead Tuckahoe",
        "Cassiabarktree Twig",
        "Florists Dendranthema",
        "Ginseng Root",
        "Mongolian Milkvetch Root",
        "Common Anemarrhena",
        "Radix Rehmanniae",
        "Fortune's Drynaria Rhizome",
        "Ma-yuen Jobstears Seed",
        "Chinese Taxillus Herb"
    )

    private val scientificName = arrayOf(
        "學名",
        "Scutellaria baricalensis GEORGI",
        "Rehmannia glutinosa Libosch.",
        "Codonopsis pilosula(Franch.) Nannf.",
        "Glycyrrhiza uralensis FISCH.",
        "Achyranthes bidentata BLUME",
        "Ophiopogon japonicus KER-GAWLER",
        "Atractylodes macrocephala Koidz.",
        "Pueraria lobata(WILLD.)OHWI",
        "Morus alba L.",
        "Platycodon grandiflorum(JACQ.)A. DC.",
        "CORNI FRUCTUS",
        "Polygonum multiflorum THUNB.",
        "Paeonia veitchii Lynch",
        "Cinnamomum cassia Presl",
        "Angelica sinensis (Oliv.) Diels",
        "Ligusticum chuaxiong HORT.",
        "Carthamus tinctorius LINN",
        "Citrus aurantium L.",
        "Ziziphus jujuba Mill.",
        "Asarum sieboldii Miq.",
        "Forsythia suspensa VAHL.",
        "Dioscorea oppoita THUNB.",
        "Tussilago farfara LINN.",
        "ZINGIBERIS RHIZOMA",
        "ACONITI LATERALIS RADIX",
        "Prunus persica (L)BATSCH",
        "Aster tataricus L. f.",
        "Lindera strychnifolia(SIEB. et ZUCC.)F.VILLARS",
        "Pooaeonia suffruticosa ANDR.",
        "Lycium chinense MILL",
        "Amomun kravanh Pirreex Gagmep.",
        "Poria cocos(SCHW.) WOLF.",
        "Cinnamomum cassia Presl",
        "Chrysanthemum morifolium RAMAT.",
        "Panax ginseng C. A. Mey.",
        "Astragalus mongholicus BUNGE",
        "Anemarrhena aspodeloidea BUNGE.",
        "Rehmannia glutinosa Libosch.",
        "Drynaria fortunei（KUNTZE）J. SMITH",
        "Coix lacryma-jobi L. var. ma-guen (Roman) Stapf",
        "Viscum coloratum (Komar.) NakaiTAXILLI RAMULUS"
    )

    private val tasteAndTropism = arrayOf(
        "性味歸經",
        "苦寒。入心、肺、肝、膽、大腸五經。",
        "甘微溫。入心、肝、腎三經。",
        "甘平。入脾、肺二經。",
        "甘平無毒。通行十二經。",
        "味苦酸，性平。歸肝、腎經。",
        "甘微苦微寒。入心、肺、胃三經。",
        "苦、甘，溫。歸脾、胃經。",
        "辛甘平無毒。入脾、胃二經。",
        "甘寒無毒。入肺經。",
        "苦辛微溫。入肺經。",
        "酸澀微溫。入肝、賢二經。",
        "苦甘澀溫。入肝、腎二經。",
        "苦，微寒。歸肝經。",
        "辛甘大熱有小毒。入肝、腎二經。",
        "味甘、辛、苦。性溫。",
        "辛溫無毒。入肝、膽、心胞三經。",
        "辛溫無毒。入心、肝二經。",
        "苦酸寒。",
        "甘，溫。歸脾、胃經。",
        "辛溫無毒。入心、肺、肝、腎四經。",
        "苦微寒無毒。入心、膽、三焦、大腸四經。",
        "甘微苦溫。入脾、肺、胃、腎四經。",
        "辛溫無毒。入肺經。",
        "味辛，性熱。歸脾、胃、心、肺經。",
        "辛甘大熱有毒。通行十二經。",
        "苦甘平無毒。入心、肝二經。",
        "辛、甘、苦，溫。歸肺經。",
        "辛溫無毒。入脾、胃、肺、腎四經。",
        "苦辛寒。入心、肝、腎三經。",
        "甘平無毒。入肝、肺、腎三經。",
        "辛，溫。歸肺、脾、胃經。",
        "甘淡平。入心、脾、肺、腎四經。",
        "辛甘溫無毒。入肺、心、膀胱三經。",
        "甘苦微寒無毒。入肺、肝、腎三經。",
        "甘、微苦，微溫。歸心、肺、脾經。",
        "甘溫。入脾、肺二經。",
        "苦寒無毒。入肺、腎、胃三經。",
        "甘苦寒。入心、肝、腎、小腸四經。",
        "苦溫無毒。入肝、腎二經。",
        "甘、淡、微寒。歸脾、胃、肺經。",
        "味苦、甘，性平。入肝、腎二經。"
    )

    private val feature = arrayOf(
        "功效",
        "清熱燥濕，瀉肺火解毒，消炎利尿，涼血止血，安胎。用於肺熱咳嗽，血熱妄行，濕熱下痢，胎動不安，降血壓，急性胃腸炎，高血壓，頭痛，失眠。主要為清瀉肺火。",
        "滋腎養陰，補血填髓，聰耳明目，黑髮烏髭。用於腎陰不足，腰酸腿弱，耳鳴目眩，肝血不足，遺精，婦女崩漏，月經不調，陰虛咳嗽，消渴等症。",
        "補中益氣，強壯健胃，除煩止渴。(強壯，健胃，增進新陳代謝，補血，降壓，袪痰鎮咳。)用於脾胃虛弱，氣血兩虧，食慾不振，肺氣不足，津傷口渴，貧血，糖尿病。黨參與人參比較：性能基本相同，故一般補劑中凡用人參的都可用黨參代。但由於黨參效力較人參弱，故用量要加大；又由於黨參大補元氣的力量較弱，且有降壓作用，因此，對陽氣虛脫的危重病症，仍以用人參為宜。",
        "補脾益氣，清熱解毒，潤肺止咳，為緩和及調補要藥。用於脾虛氣弱，咳嗽氣喘，癰疽瘡毒，尿道疼痛及調和藥性等。",
        "補肝腎、強筋骨、散風痹。用於風濕痹症，下肢腫痛，腰膝痠痛，折傷閃挫，鶴膝風症，口舌生瘡，久潰不斂。五淋隆閉，淋濁尿血，月經不行，行經腹痛，瘕痞結聚。足痿不行，肝腎不足，腳膝軟弱，舌紅少津，肝陽上越，頭暈目眩，半身不遂，舌強言蹇。",
        "清心潤肺，化痰止咳，健胃生津，養陰益精。用於勞熱咳血，燥咳痰粘，心煩口渴，津枯便秘等症。",
        "補氣健脾，燥濕利水，止汗，安胎。用於脾胃氣虛，運化無力，食少便溏，脘腹脹滿，肢軟神疲等證。",
        "發表退熱，生津止渴，解痙，透疹，止瀉。解熱，擴張心冠脈，增加腦血流量。用於熱性病發熱無汗，頭項強痛，熱病口渴，消渴，斑疹不透，脾虛泄瀉，高血壓，冠心病。",
        "解熱利尿，袪痰止咳平喘，消炎消腫。肺熱喘滿，熱渴，停水消渴，水腫腹脹，小便不利，支氣管炎。",
        "宣肺解表，溫中發散風寒，袪痰止咳，消腫排膿。(袪痰，鎮咳)用於風寒風熱咳嗽，氣管支炎，扁桃腺炎，咽喉炎，失聲，肺膿瘍，腸鳴腹瀉，化膿性疾患。",
        "滋養強壯，補腎固精，陽萎遺精，明目，補肝補血，收斂止汗，自汗盜汗，腰膝痠痛，頭暈目眩，耳鳴耳聾，小便頻多，月經過多。用於腰膝酸痛，眩暈耳鳴，陽痿，遺精，月經過多，虛汗過多等症。主要用於肝腎不足而有滑脫証候者。",
        "補益肝腎，滋陰強壯，益精血，壯筋骨，烏髭髮，緩瀉。 (降膽固醇，抗動脈硬化，瀉下，抗病毒，類似腎上腺皮質激素樣的作用。)用於氣血肝腎虧損，遺精帶下，腰膝酸痛，鬚髮早白(製用)；久瘧，癰疽瘰癘，腸燥便秘(生用)。動脈硬化、高血壓病、冠心病。",
        "清熱涼血，散瘀止痛。主邪氣腹痛，除血痹，破堅積，寒熱疝瘕，止痛，利小便。",
        "補命門火，溫脾胃，暖腰腎，溫通血脈，散寒止痛，溫煦氣血。(擴張血管，健胃。)用於下元虛冷，腎虛腰痛，寒疝疼痛，脾胃虛寒，腹脹胃痛，消化不良，腹瀉腹痛，婦女經寒血滯，產後瘀滯腹痛，陰疽，癰瘍膿成不潰或久潰，氣血虛證。",
        "治月經不調，經閉腹痛，癥瘕結聚，崩漏；血虛頭痛，眩暈，痿痺；腸燥便難，赤痢後重；癰疽瘡竊，跌撲損傷。",
        "驅風止痛，活血行氣，鎮靜，通經。貧血，冷感症，月經不調，婦人經血病，頭痛眩暈，肋痛腹痛，風濕痺痛，半身不遂。",
        "破瘀，活血，通經。(興奮子宮，降壓，擴張血管。)用於經血不調，產後腹痛，癥瘕，外傷瘀血腫痛，癰疽腫痛等症。大劑量：活血破瘀。小劑量：養血和血。",
        "利膈寬胸，破氣消積，袪痰除痞。用於氣郁胸腹，食積痰滯不消，痞滿脹痛，嘔逆咳嗽，腹痛便秘；瀉痢不暢，裡急後重證；痰濁阻塞。",
        "補中益氣，養血安神，緩和藥性。",
        "細辛根散風袪寒，通竅止痛，下氣袪痰，行水氣。解熱發汗。用於感冒風寒頭痛，鼻塞頭痛，風寒濕痹，氣逆痰多喘咳，牙痛，口瘡，喉痺，鼻淵。",
        "清心解熱，消腫散結，利尿。(抗菌，抗病毒，強心，利尿。)用於外感風熱，急性熱病初起。煩熱神昏，癰腫瘡毒，瘰癘等症。為治療熱病和瘡癰的重要藥物。",
        "滋養強壯，補氣養陰，益筋骨，健忘煩熱，生津止渴，虛勞咳嗽，補脾肺腎，開胃，脾虛泄瀉，久痢，消渴遺精，帶下，小便頻數，夜尿，糖尿病。(滋養強壯，生津止渴，濇精止瀉，補脾肺，益筋骨。脾虛泄瀉，久痢，虛勞咳嗽，健忘煩熱，遺精，帶下，小便頻數，夜尿，盜汗，糖尿病。)",
        "潤肺止咳，消痰下氣。(有一定的鎮咳效力，但袪痰作用不顯著。)風寒喘咳，痰多，勞嗽，咯血。為止咳常用藥。",
        "溫中散寒，健胃鎮痛，溫經止血，回陽，宣通脈。(促進血液循環，健胃止嘔，升壓。) 用於中焦虛寒，吐瀉腹痛，肢冷脈微，寒飲咳嗽，胃脘冷痛，寒濕痹痛，亡陽。※乾薑善於溫中而袪裏寒。乾薑溫中散寒，主要作用於腸胃，其效力較強勁而持久，附子大熱回陽，強心作用較顯著，作用於全身，其力較迅速而不久留。",
        "回陽益火，散寒除濕。用於陰盛格陽，大汗亡陽，吐利厥逆，心腹冷痛，脾泄冷痢，腳氣水腫，小兒慢驚，風寒濕痹，痿躄拘攣，陰疽瘡漏及一切沉寒痼冷之疾等証。",
        "活血行瘀，潤燥滑腸。(鎮痛，消炎，解毒，通便。)用於經閉癥瘕，蓄血，腹痛，外傷瘀血及腸癰，腸燥便秘。為袪瘀常用藥。",
        "潤肺化痰止咳。",
        "舒氣，溫中，散寒，止痛，溫腎。用於胸脅脹痛，脘腹冷痛，經行腹痛，反胃吐食，疝氣脹痛，小便頻數，遺尿，上氣喘急等症。較常用於氣滯、氣逆引起的腹部痛證，尤以治下腹脹痛效果更佳。",
        "清熱涼血，活血化瘀，清退虛熱。(抗菌，降壓。)用於斑疹吐衄，血滯經閉，經前發熱，癰腫瘡毒，損傷瘀血、陰虛發熱、骨蒸勞熱，頭痛，腰痛，關節痛，月經痛，高血壓。主要用於清瀉肝火和涼血消瘀。",
        "補肝腎，強筋骨，潤肺，明目。(滋養，強壯，輕微的抑制脂肪在肝細胞內沉積，促進肝細胞新生。)用於肝腎陰虛，腰膝酸軟，虛勞咳嗽，頭目眩暈，目赤生翳，消渴等症。為平補肝腎的常用藥，不寒不熱，陰虛陽虛都可用，但較多用於陰虛。",
        "化濕行氣，溫中止嘔。",
        "利水滲濕，補脾健胃，寧心安神，生津止渴。(利尿，增強免疫，抗腫瘤，鎮靜，抗菌。)用於水腫，小便不利，痰飲，脾氣虛弱，嘔吐，腹瀉，遺精，頭目眩暈，口乾舌燥，驚悸，健忘，失眠。",
        "助陽，發汗解肌，溫經通絡，溫化水濕，驅風鎮痛。用於風寒感冒，風寒濕痹，婦女經閉腹痛，痰飲蓄水，適用於感冒風寒之表虛自汗者。",
        "疏散風熱，平抑肝陽，清肝明目，清熱解毒。消炎，利尿，抗菌。用於頭目風熱，眩暈，頭痛目赤，疔瘡腫毒。",
        "大補元氣，補脾益肺，生津，安神。",
        "補氣升陽，固表止汗，利水消腫，托毒排膿。(強壯作用、利尿、抗腎炎、降壓、抗菌、保護肝臟、防止肝糖元減少。)用於表虛自汗，脾虛泄瀉，脫肛，中氣下陷，消渴，癰疽久不收口等症。黃耆善補肌表之氣，適宜表虛者用；人參善補五臟之氣，適宜裡虛者用。參、耆合用，補益力更全面而加強。",
        "清熱瀉火除煩，清肺滋腎。解熱，抗菌，鎮靜。用於熱病口渴，肺熱咳嗽，陰虛燥熱的虛汗骨蒸，消渴，大便燥結，小便黃短。",
        "清熱涼血，養陰生津，平血逆，降血糖。血虛發熱，吐衄下血，傷寒瘟疫，斑疹，咽喉腫痛，婦人崩中，產後血虛腹痛，高血壓，糖尿病。清熱涼血，止血，養陰。(止血，強心，利尿，降血糖。)用於陰虛發熱，熱病傷陰，煩渴、骨蒸，斑疹，吐衄下血，婦女月經不調，胎動不安等症。※.生地性涼，用於清熱涼血；熟地性溫，用於補血滋陰，當清熱而又要照顧體虛時，可生、熟地並用。",
        "補肝腎，續筋骨，活血止痛。用於腰膝筋骨酸痛，外傷瘀血作痛，腎虛久瀉，耳鳴，牙痛。",
        "利水滲濕，健脾，除痹，清熱排膿。",
        "袪風濕，補肝腎，強筋骨，養血安胎。(降壓、舒張冠脈及增加冠脈流量，降膽固醇，利尿，抗菌，抗病毒。)用於風濕痹痛，腰膝痠軟疼痛，胎動不安，高血壓，皮膚乾燥症等。"
    )
}