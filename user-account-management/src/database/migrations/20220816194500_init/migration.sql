/*
  Warnings:

  - The `nationality` column on the `User` table would be dropped and recreated. This will lead to data loss if there is data in the column.

*/
-- CreateEnum
CREATE TYPE "Nationalities" AS ENUM ('AFGHAN', 'ALBANIAN', 'ALGERIAN', 'AMERICAN', 'ANDORRAN', 'ANGOLAN', 'ANTIGUANS', 'ARGENTINEAN', 'ARMENIAN', 'AUSTRALIAN', 'AUSTRIAN', 'AZERBAIJANI', 'BAHAMIAN', 'BAHRAINI', 'BANGLADESHI', 'BARBADIAN', 'BARBUDANS', 'BATSWANA', 'BELARUSIAN', 'BELGIAN', 'BELIZEAN', 'BENINESE', 'BHUTANESE', 'BOLIVIAN', 'BOSNIAN', 'BRAZILIAN', 'BRITISH', 'BRUNEIAN', 'BULGARIAN', 'BURKINABE', 'BURMESE', 'BURUNDIAN', 'CAMBODIAN', 'CAMEROONIAN', 'CANADIAN', 'CAPE_VERDEAN', 'CENTRAL_AFRICAN', 'CHADIAN', 'CHILEAN', 'CHINESE', 'COLOMBIAN', 'COMORAN', 'CONGOLESE', 'COSTA_RICAN', 'CROATIAN', 'CUBAN', 'CYPRIOT', 'CZECH', 'DANISH', 'DJIBOUTI', 'DOMINICAN', 'DUTCH', 'EAST_TIMORESE', 'ECUADOREAN', 'EGYPTIAN', 'EMIRIAN', 'EQUATORIAL_GUINEAN', 'ERITREAN', 'ESTONIAN', 'ETHIOPIAN', 'FIJIAN', 'FILIPINO', 'FINNISH', 'FRENCH', 'GABONESE', 'GAMBIAN', 'GEORGIAN', 'GERMAN', 'GHANAIAN', 'GREEK', 'GRENADIAN', 'GUATEMALAN', 'GUINEA_BISSAUAN', 'GUINEAN', 'GUYANESE', 'HAITIAN', 'HERZEGOVINIAN', 'HONDURAN', 'HUNGARIAN', 'ICELANDER', 'INDIAN', 'INDONESIAN', 'IRANIAN', 'IRAQI', 'IRISH', 'ISRAELI', 'ITALIAN', 'IVORIAN', 'JAMAICAN', 'JAPANESE', 'JORDANIAN', 'KAZAKHSTANI', 'KENYAN', 'KITTIAN_AND_NEVISIAN', 'KUWAITI', 'KYRGYZ', 'LAOTIAN', 'LATVIAN', 'LEBANESE', 'LIBERIAN', 'LIBYAN', 'LIECHTENSTEINER', 'LITHUANIAN', 'LUXEMBOURGER', 'MACEDONIAN', 'MALAGASY', 'MALAWIAN', 'MALAYSIAN', 'MALDIVAN', 'MALIAN', 'MALTESE', 'MARSHALLESE', 'MAURITANIAN', 'MAURITIAN', 'MEXICAN', 'MICRONESIAN', 'MOLDOVAN', 'MONACAN', 'MONGOLIAN', 'MOROCCAN', 'MOSOTHO', 'MOTSWANA', 'MOZAMBICAN', 'NAMIBIAN', 'NAURUAN', 'NEPALESE', 'NEW_ZEALANDER', 'NI_VANUATU', 'NICARAGUAN', 'NIGERIEN', 'NORTH_KOREAN', 'NORTHERN_IRISH', 'NORWEGIAN', 'OMANI', 'PAKISTANI', 'PALAUAN', 'PANAMANIAN', 'PAPUA_NEW_GUINEAN', 'PARAGUAYAN', 'PERUVIAN', 'POLISH', 'PORTUGUESE', 'QATARI', 'ROMANIAN', 'RUSSIAN', 'RWANDAN', 'SAINT_LUCIAN', 'SALVADORAN', 'SAMOAN', 'SAN_MARINESE', 'SAO_TOMEAN', 'SAUDI', 'SCOTTISH', 'SENEGALESE', 'SERBIAN', 'SEYCHELLOIS', 'SIERRA_LEONEAN', 'SINGAPOREAN', 'SLOVAKIAN', 'SLOVENIAN', 'SOLOMON_ISLANDER', 'SOMALI', 'SOUTH_AFRICAN', 'SOUTH_KOREAN', 'SPANISH', 'SRI_LANKAN', 'SUDANESE', 'SURINAMER', 'SWAZI', 'SWEDISH', 'SWISS', 'SYRIAN', 'TAIWANESE', 'TAJIK', 'TANZANIAN', 'THAI', 'TOGOLESE', 'TONGAN', 'TRINIDADIAN_OR_TOBAGONIAN', 'TUNISIAN', 'TURKISH', 'TUVALUAN', 'UGANDAN', 'UKRAINIAN', 'URUGUAYAN', 'UZBEKISTANI', 'VENEZUELAN', 'VIETNAMESE', 'WELSH', 'YEMENITE', 'ZAMBIAN', 'ZIMBABWEAN');

-- AlterTable
ALTER TABLE "User" DROP COLUMN "nationality",
ADD COLUMN     "nationality" "Nationalities" NOT NULL DEFAULT 'RWANDAN';
