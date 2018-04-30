package com.iisigroup.colabase.service;

import java.util.List;
import java.util.Map;

/**
 * Created by jackblackevo on 2017/4/26.
 */
public interface AddressSplitterService {
  Map<String, String> splitAddress(List<String> roadList, String otherAddress);
}
