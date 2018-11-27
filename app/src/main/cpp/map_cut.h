#pragma once

#include <vector>


#include"_ContourFinder.h"


std::vector<std::vector<_PointI32>> MapCut_ErodeContourBox(unsigned char *mapData, int OrgW, int OrgH, float xMin, float yMin, float resolution);

std::vector<std::vector<_PointI32>> MapCut_Hough(unsigned char *mapData, int OrgW, int OrgH, float xMin, float yMin, float resolution);