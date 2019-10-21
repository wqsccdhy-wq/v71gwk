/**
 * $Author: gaohang $
 * $Rev: 35757 $
 * $Date:: #$:
 *
 * Copyright (C) 2012 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 */
package com.seeyon.ctp.privilege.controller;

import com.seeyon.ctp.common.controller.BaseController;

public class ResourceController extends BaseController {

	/** *//*
	PrivilegeManage privilegeManage;

	*//** *//*
	FileManager fileManager;

	protected ResourceManager resourceManager;

	protected ResourceManager getResourceManager() {
		if (resourceManager == null) {
			resourceManager = (ResourceManager) AppContext
					.getBean("resourceManager");
		}

		return resourceManager;
	}

	protected MenuManager menuManager;

	protected MenuManager getMenuManager() {
		if (menuManager == null) {
			menuManager = (MenuManager) AppContext.getBean("menuManager");
		}

		return menuManager;
	}

	
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.seeyon.ctp.common.controller.BaseController#index(javax.servlet.http
	 * .HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return new ModelAndView("apps/privilege/resource/resourceList");
	}

	public ModelAndView create(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return new ModelAndView("apps/privilege/resource/resourceNew");
	}

	public ModelAndView select(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		request.setAttribute("ffcmd", request.getParameter("cmd"));
		request.setAttribute("appResCategory",
				request.getParameter("appResCategory"));
		request.setAttribute("productVersion",
				request.getParameter("productVersion"));
		return new ModelAndView("apps/privilege/resource/resourceList4Select");
	}

	public ModelAndView importResSubmit(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String fileid = request.getParameter("fileid");
		if (fileid != null) {
			String[] files = fileid.split(",");
			StringBuilder result = new StringBuilder();
			File file = null;
			for (String path : files) {
				file = fileManager.getFile(Long.parseLong(path), new Date());
				if (file.exists()) {
					try {
						result.append(processXlsx(file));
					} catch (Exception e) {
						result.append(e.getMessage());
					}
					// 处理完成后删除文件
					file.delete();
				}
			}
			request.setAttribute("cmd", "importresult");
			request.setAttribute("results", result.toString());
		}
		return new ModelAndView("apps/privilege/resource/resourceImport");
	}

	*//**
	 * @param info
	 * @param value
	 * @return
	 *//*
	private String checkString(String info, String value, int length) {
		StringBuilder result = new StringBuilder();
		if (!StringUtil.checkNull(value)) {
			String mark = "!@#$%^*()+";
			char[] chars = value.toCharArray();
			for (char c : chars) {
				if (mark.indexOf(c) != -1) {
					result.append(info).append("包含特殊字符。<br>");
					break;
				}
			}
			if (value.length() > length) {
				result.append(info).append("长度超过" + length + "。<br>");
			}
		}
		return result.toString();
	}

	*//**
	 * 批量导入资源时处理上传的excel文件
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws BusinessException
	 *//*
	private String processXlsx(File file) throws FileNotFoundException,
			IOException, BusinessException {
		StringBuilder results = new StringBuilder();
		InputStream fs = new FileInputStream(file);
		XSSFWorkbook workbook = new XSSFWorkbook(fs);
		XSSFSheet st = workbook.getSheetAt(0);
		XSSFRow aRow = null;
		String value = null;
		List<PrivResource> reses = new ArrayList<PrivResource>();
		PrivResource res = null;
		Map<String, String> enterSources = new HashMap<String, String>();
		Map<String, List<String>> belongToSources = new HashMap<String, List<String>>();
		StringBuilder enterSourceKey = null;
		int successTotal = 0;
		for (int i = 1; i <= st.getLastRowNum(); i++) {
			boolean isEmptyRow = true;
			boolean isResNameEmpty = false;
			boolean isResTypeEmpty = false;
			boolean isResUrlEmpty = false;
			boolean isResCodeExist = false;
			boolean isResNameExist = false;
			if (null != st.getRow(i)) {
				res = new PrivResource();
				enterSourceKey = new StringBuilder();
				aRow = st.getRow(i);
				int cols = -1;
				// 一级菜单
				value = getCellXlsx(aRow.getCell(++cols));
				enterSourceKey.append(value);
				if (!StringUtil.checkNull(value)) {
					isEmptyRow = false;
				}
				// 二级菜单
				value = getCellXlsx(aRow.getCell(++cols));
				enterSourceKey.append(value);
				if (!StringUtil.checkNull(value)) {
					isEmptyRow = false;
				}
				// 资源名称
				value = getCellXlsx(aRow.getCell(++cols));
				if (!StringUtil.checkNull(value)) {
					isEmptyRow = false;
					PrivResource resCheck = new PrivResource();
					resCheck.setResourceName(value);
					List<PrivResourceBO> resesCheck = privilegeManage
							.findResourcesByCode(resCheck);
					if (resesCheck != null && resesCheck.size() > 0) {
						isResNameExist = true;
					}
					String checkString = checkString("第" + (i + 1) + "行资源名称",
							value, 50);
					if (!StringUtil.checkNull(checkString)) {
						results.append(checkString);
						continue;
					}
				} else {
					isResNameEmpty = true;
				}
				res.setResourceName(value);
				// 资源编码
				value = getCellXlsx(aRow.getCell(++cols));
				if (!StringUtil.checkNull(value)) {
					isEmptyRow = false;
					PrivResource resCheck = new PrivResource();
					resCheck.setResourceCode(value);
					List<PrivResourceBO> resesCheck = privilegeManage
							.findResourcesByCode(resCheck);
					if (resesCheck != null && resesCheck.size() > 0) {
						isResCodeExist = true;
					}
				}
				res.setResourceCode(value);
				// 是否为菜单入口资源
				value = getCellXlsx(aRow.getCell(++cols));
				if (!StringUtil.checkNull(value)) {
					isEmptyRow = false;
				}
				res.setExt1(BooleanEnums.trueflag.getText().equals(value) ? ResourceCategoryEnums.enterresource
						.getValue() : ResourceCategoryEnums.otherresource
						.getValue());
				// 归属资源
				if (BooleanEnums.trueflag.getText().equals(value)) {
					enterSources.put(enterSourceKey.toString(),
							res.getResourceCode());
				} else {
					String resCode = enterSources
							.get(enterSourceKey.toString());
					if (!StringUtil.checkNull(resCode)) {
						List<String> resCodes = belongToSources.get(resCode);
						if (resCodes == null) {
							resCodes = new ArrayList<String>();
						}
						resCodes.add(res.getResourceCode());
						belongToSources.put(resCode, resCodes);
					}
				}
				// 资源类型, 0:普通url, 1:ajax
				value = getCellXlsx(aRow.getCell(++cols));
				if (!StringUtil.checkNull(value)) {
					isEmptyRow = false;
				} else {
					isResTypeEmpty = true;
				}
				res.setResourceType(ResourceTypeEnums.url.getText().equals(
						value) ? ResourceTypeEnums.url.getKey()
						: ResourceTypeEnums.ajax.getKey());
				// 资源链接
				value = getCellXlsx(aRow.getCell(++cols));
				if (!StringUtil.checkNull(value)) {
					isEmptyRow = false;
					String checkString = checkString("第" + (i + 1) + "行资源链接",
							value, 255);
					if (!StringUtil.checkNull(checkString)) {
						results.append(checkString);
						continue;
					}
				} else {
					isResUrlEmpty = true;
				}
				res.setNavurl(value);
				// 是否权限控制
				value = getCellXlsx(aRow.getCell(++cols));
				if (!StringUtil.checkNull(value)) {
					isEmptyRow = false;
				} else {
					value = BooleanEnums.trueflag.getText();
				}
				res.setControl(BooleanEnums.trueflag.getText().equals(value));
				// 所属模块
				value = getCellXlsx(aRow.getCell(++cols));
				if (!StringUtil.checkNull(value)) {
					isEmptyRow = false;
				}
				// 资源层级
				value = getCellXlsx(aRow.getCell(++cols));
				if (!StringUtil.checkNull(value)) {
					isEmptyRow = false;
				}
				// 是否系统资源
				boolean isDevelop = AppContext.isRunningModeDevelop();
				if (isDevelop) {
					res.setResourceCategory(SystemResourceCategoryEnums.ststemfront
							.getKey());
				} else {
					res.setResourceCategory(CustomResourceCategoryEnums.customfront
							.getKey());
				}
				if (!isEmptyRow) {
					if (isResNameEmpty) {
						results.append("第" + (i + 1) + "行资源名称为空。<br>");
					} else if (isResTypeEmpty) {
						results.append("第" + (i + 1) + "行资源类型为空。<br>");
					} else if (isResUrlEmpty) {
						results.append("第" + (i + 1) + "行资源链接为空。<br>");
					} else if (isResCodeExist) {
						results.append("第" + (i + 1) + "行资源编码已存在。<br>");
					} else if (isResNameExist) {
						results.append("第" + (i + 1) + "行资源名称已存在。<br>");
					} else {
						reses.add(res);
						successTotal++;
					}
				}
			}
		}
		results.append("成功导入" + successTotal + "条数据。");
		privilegeManage.createResourceBatch(reses);
		privilegeManage.updateResource4Import(belongToSources);
		return results.toString();
	}

	public String getCellXlsx(XSSFCell cell) {
		if (cell == null)
			return "";
		switch (cell.getCellType()) {
		case XSSFCell.CELL_TYPE_NUMERIC:
			return cell.getNumericCellValue() + "";
		case XSSFCell.CELL_TYPE_STRING:
			return cell.getStringCellValue();
		case XSSFCell.CELL_TYPE_FORMULA:
			return cell.getCellFormula();
		case XSSFCell.CELL_TYPE_BLANK:
			return "";
		case XSSFCell.CELL_TYPE_BOOLEAN:
			return cell.getBooleanCellValue() + "";
		case XSSFCell.CELL_TYPE_ERROR:
			return cell.getErrorCellValue() + "";
		}
		return "";
	}

	*//**
	 * 编辑资源信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 *//*
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long resourceId = Long.parseLong(request.getParameter("id"));
		PrivResource res = privilegeManage.findResourceById(resourceId);
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put("id", String.valueOf(res.getId()));
		resMap.put("resourceName", res.getResourceName());
		resMap.put("resourceCode", res.getResourceCode());
		resMap.put("resourceType", String.valueOf(res.getResourceType()));
		resMap.put("navurl", res.getNavurl());
		resMap.put("resourceOrder", res.getResourceOrder());
		resMap.put("resourceCategory", res.getResourceCategory());
		resMap.put("moduleid", res.getModuleid());
		resMap.put("show", res.isShow());
		resMap.put("isControl", res.isControl() ? 1 : 0);
		// 资源类型
		resMap.put("ext1", res.getExt1() == null ? 2 : res.getExt1());
		String belongtoId = res.getExt2();
		if (belongtoId == null) {
			belongtoId = "0";
		}
		// 资源默认为前台应用资源
		Integer mainResId = res.getExt4() == null ? 1 : res.getExt4();
		resMap.put("mainResId", res.getExt3());
		if (AppResourceCategoryEnums.ForegroundShortcut.getKey() == mainResId) {
			res = privilegeManage
					.findResourceById(Long.parseLong(res.getExt3()));
			if (res != null) {
				resMap.put("mainResName", res.getResourceName());
				resMap.put("mainResUrl", res.getNavurl());
			}
		}
		resMap.put("ext4", mainResId);
		res = privilegeManage.findResourceById(Long.valueOf(belongtoId));
		resMap.put("belongto", res == null ? "" : res.getResourceName());
		resMap.put("belongtoId", belongtoId);
		request.setAttribute("ffmyfrm", resMap);
		return new ModelAndView("apps/privilege/resource/resourceNew");
	}

	*//**
	 * 新建和编辑后保存
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 *//*
	public ModelAndView save(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PrivResource res = new PrivResource();
		@SuppressWarnings("rawtypes")
		Map params = ParamUtil.getJsonParams();
		for (Object entryObj : params.entrySet()) {
			if (entryObj instanceof Map.Entry) {
				@SuppressWarnings("rawtypes")
				Map.Entry entry = (Map.Entry) entryObj;
				if ("id".equals(entry.getKey())) {
					String id = String.valueOf(entry.getValue());
					if (!"".equals(id)) {
						res.setId(Long.parseLong(id));
					}
				}
				if ("resourceName".equals(entry.getKey())) {
					res.setResourceName(String.valueOf(entry.getValue()));
				}
				if ("resourceCode".equals(entry.getKey())) {
					res.setResourceCode(String.valueOf(entry.getValue()));
				}
				if ("resourceType".equals(entry.getKey())) {
					res.setResourceType(Integer.parseInt(String.valueOf(entry
							.getValue())));
				}
				if ("resourceCategory".equals(entry.getKey())) {
					res.setResourceCategory(Integer.parseInt(String
							.valueOf(entry.getValue())));
				}
				if ("navurl".equals(entry.getKey())) {
					res.setNavurl(String.valueOf(entry.getValue()));
				}
			}
		}
		if (res.getId() == 0) {
			privilegeManage.createResource(res);
		} else {
			privilegeManage.updateResource(res);
		}
		return new ModelAndView("apps/privilege/resource/resourceNew");
	}

	*//**
	 * 资源树
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 *//*
	public ModelAndView getTree(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// 查询条件
		String memberId = request.getParameter("memberId");
		String accountId = request.getParameter("accountId");
		String roleId = request.getParameter("roleId");
		// 所属产品线版本
		String version = request.getParameter("version");
		// 操作命令
		String cmd = request.getParameter("cmd");
		// true 显示所有资源, false 只显示选择资源
		String showAll = request.getParameter("showAll");
		// true 允许拖拽, false 不允许拖拽
		String drag = request.getParameter("drag");
		// true 分配界面, false 资源管理界面
		String isAllocated = request.getParameter("isAllocated");
		// 资源应用类型
		String appResCategory = request.getParameter("appResCategory");
		// 后台管理资源列表
		List<PrivTreeNodeBO> treeNodes4Back = new ArrayList<PrivTreeNodeBO>();
		// 前台应用资源列表
		List<PrivTreeNodeBO> treeNodes4Front = new ArrayList<PrivTreeNodeBO>();
		getTreeNodes(memberId, accountId, roleId, showAll, version,
				appResCategory, isAllocated, treeNodes4Back, treeNodes4Front);

		// 菜单页面展示树的情况，只保留一棵树不区分前后台
		treeNodes4Front.addAll(treeNodes4Back);
		// 添加根节点
		PrivTreeNodeBO node = new PrivTreeNodeBO();
		node.setIdKey("menu_0");
		node.setNameKey("菜单资源树");
		node.setpIdKey("0");
		if (!CollectionUtils.isEmpty(treeNodes4Front)) {
			treeNodes4Front.add(node);
		}
		request.setAttribute("fftreefront", treeNodes4Front);
		if (showAll != null) {
			List<PrivTreeNodeBO> treeNodes4BackAll = new ArrayList<PrivTreeNodeBO>();
			List<PrivTreeNodeBO> treeNodes4FrontAll = new ArrayList<PrivTreeNodeBO>();
			getTreeNodes(null, null, null, showAll, version, appResCategory,
					isAllocated, treeNodes4BackAll, treeNodes4FrontAll);
			request.setAttribute("fftreeback", treeNodes4BackAll);
			// 添加根节点
			treeNodes4FrontAll.add(node);
			request.setAttribute("fftreefront", treeNodes4FrontAll);
			request.setAttribute("fftreebackCheck", treeNodes4Back);
			request.setAttribute("fftreefrontCheck", treeNodes4Front);
		}
		request.setAttribute("ffdrag", drag);
		request.setAttribute("ffcmd", cmd);
		request.setAttribute("ffroleId", roleId);
		return new ModelAndView("apps/privilege/resource/resourceTree");
	}

	private void getTreeNodes(String memberId, String accountId, String roleId,
			String showAll, String version, String appResCategory,
			String isAllocated, List<PrivTreeNodeBO> treeNodes4Back,
			List<PrivTreeNodeBO> treeNodes4Front) throws BusinessException {
	    User user = AppContext.getCurrentUser();
		// 当前登录人员拥有的所有菜单列表
		List<PrivMenuBO> menus = new ArrayList<PrivMenuBO>();
		// 当前登录人员拥有的所有资源列表
		List<PrivResource> resesAll = null;
		// 不可修改的角色资源关系列表
		Map<Long, HashSet<Long>> roleResUnEditable = null;
		//不可编辑的资源
		HashSet<Long> resUnEidtable = privilegeManage.findUnModifiable();
		//
		Map<Long, List<Long>> menuResMap = null;
		// 如果人员ID和单位ID不为空则为查看所拥有的资源
		if (memberId != null && accountId != null) {
			Long memId = Long.parseLong(memberId);
			Long accId = Long.parseLong(accountId);
			// 获得当前人员关联的菜单
			menus = privilegeManage.getMenusOfMember(memId, accId);
			if (menus != null && menus.size() != 0) {
				// 获得当前人员关联的所有资源
				resesAll = privilegeManage.getResourcesOfMember(memId, accId);
			}
		} else if (roleId != null) {
			Long[] roleIds = new Long[1];
			if (roleId != null) {
				roleIds[0] = Long.parseLong(roleId);
			}
			// 获得当前角色包含的菜单
			Map<Long, PrivMenuBO> menuMap = privilegeManage
					.getMenuByRole(roleIds);
			if (menuMap != null && menuMap.size() != 0) {
				menus = new ArrayList(menuMap.values());
				Collections.sort(menus, CompareSortMenu.getInstance());

				// 获得当前角色关联的所有资源
				resesAll = privilegeManage.getResourceByRole(roleIds);
			}
			menuResMap = privilegeManage.getMenuResourceByRole(roleIds);
			// 查找不能修改的角色资源关系
			roleResUnEditable = privilegeManage
					.findUnModifiableRoleResByRole(Long.parseLong(roleId));
		} else if (showAll != null) {
			// 如果人员ID和单位ID为空则为查看所有资源
			PrivMenuBO menu = new PrivMenuBO();
			if (!StringUtil.checkNull(appResCategory)) {
				menu.setExt4(Integer.parseInt(appResCategory));
			}
			menus = privilegeManage.findMenus(menu);
		} else {
			// 如果人员ID和单位ID为空则为查看所有资源
			PrivMenuBO menu = new PrivMenuBO();
			if (!StringUtil.checkNull(version)) {
				menu.setExt3(version);
			}
			if (!StringUtil.checkNull(appResCategory)) {
				menu.setExt4(Integer.parseInt(appResCategory));
			}
			menus = privilegeManage.findMenus(menu);
		}
		if (menus != null && menus.size() != 0) {
			PrivTreeNodeBO node = null;
			List<PrivResource> reses = null;
			HashSet<Long> resesUnEidtable = null;
			String menuType = null;
			List<PrivTreeNodeBO> result = null;
			List<Long> resIds = null;
			String sysback = MenuTypeEnums.systemback.getValue();
			String appfront = MenuTypeEnums.applicationfront.getValue();

            // 过滤停用和不可分配的菜单
            if (isAllocated != null && isAllocated.equals("true")) {
                List<PrivMenuBO> dislist = getMenuManager().getConfigDisableMenu();
                if(null != dislist) {
                    dislist.addAll(getMenuManager().getAllocatedDisableMenu());
                    if (dislist != null) {
                        for (PrivMenuBO privMenuBO : dislist) {
                            for (int i = 0; i < menus.size(); i++) {
                                PrivMenuBO res = menus.get(i);
                                if (res.getId().equals(privMenuBO.getId())) {
                                    menus.remove(i--);
                                }
                                
                            }
                        }
                    }
                    // 过滤不允许分配的菜单
                }
            }

			for (PrivMenuBO privMenuBO : menus) {
			    
			    PrivMenuBO p = getMenuManager().findById(privMenuBO.getId());
			    if(null == p || (null != p.getExt12() && p.getExt12().intValue() != 0)) {
			        continue;//不显示业务生成器菜单//OA-53898
			    }
			    
				result = new ArrayList<PrivTreeNodeBO>();
				// 将菜单对象转换为树节点对象
				node = new PrivTreeNodeBO(privMenuBO, null);
				// 菜单是否可编辑
				if (roleResUnEditable != null) {
					resesUnEidtable = roleResUnEditable.get(privMenuBO.getId());
					if (resesUnEidtable != null) {
						node.setEditKey("false");
					}
				}
				List<PrivResource> pr = privilegeManage.findResourceByMenu(privMenuBO.getId());
				if(pr!=null&&pr.size()>0){
					for (PrivResource privResource : pr) {
						if (resUnEidtable != null
								&& resUnEidtable
										.contains(privResource
												.getId())&&privResource.getExt1().equals("0")) {
							node.setEditKey("false");
							
						}
					}
					
				}
				
				
				result.add(node);
				// 从缓存获取菜单包含的资源
				reses = privilegeManage.findResourceByMenu(privMenuBO.getId());

				if (reses != null) {
					for (PrivResource privResource : reses) {
						// 入口资源和导航资源不显示
						if (ResourceCategoryEnums.enterresource.getValue()
								.equals(privResource.getExt1())
								|| ResourceCategoryEnums.naviresource
										.getValue().equals(
												privResource.getExt1())) {
							continue;
						}
						if (privResource.isControl() != null
								&& privResource.isControl()&&privResource.isShow()!=null&&privResource.isShow()) {
							// 判断拥有的权限是否包含当前资源
							if (menuResMap != null) {
								resIds = menuResMap.get(privMenuBO.getId());
							}
							// 存在两种情况
							// 1. 查看资源树结构时返回所有结点
							// 2. 查看资源分配结果时返回有权限的结点
							if (resesAll == null
									|| (resIds != null && resIds
											.contains(privResource.getId()))) {
								node = new PrivTreeNodeBO(privResource,
										privMenuBO.getId());
								// 资源是否可编辑
								if (roleResUnEditable != null) {
									if (resesUnEidtable != null
											&& resesUnEidtable
													.contains(privResource
															.getId())) {
										node.setEditKey("false");
									}
								}
								if (resUnEidtable != null
										&& resUnEidtable
												.contains(privResource
														.getId())) {
									node.setEditKey("false");
								}
								result.add(node);
							}
						}
					}
				}

				// }

				// 判断当前菜单类型
				menuType = privMenuBO.getExt1();
				if (sysback.equals(menuType)) {
					treeNodes4Back.addAll(result);
				} else if (appfront.equals(menuType)) {
					treeNodes4Front.addAll(result);
				}
			}

			// 过滤由于不可分配的菜单过滤后引起的没有子菜单且没有入口资源的菜单
			if (appResCategory != null && appResCategory.equals("1")) {
				List<PrivTreeNodeBO> templist = new ArrayList<PrivTreeNodeBO>();
				for (int i = 0; i < treeNodes4Front.size(); i++) {
					PrivTreeNodeBO privTreeNodeBO = treeNodes4Front.get(i);
					// if(privTreeNodeBO.getIdKey().split("_")[1].equals("7395095769535234350")){
					if (privTreeNodeBO == null||privTreeNodeBO.getIdKey()==null) {
						continue;
					}
					if (privTreeNodeBO.getIdKey().split("_")[0].equals("menu")) {
						if (!isHaveChildNode(privTreeNodeBO, treeNodes4Front)) {
							PrivMenuBO p = getMenuManager().findById(Long.valueOf(privTreeNodeBO.getIdKey().split("_")[1]));
							if(OrgHelper.getOrgManager().isRole(user.getId(), OrgConstants.GROUPID, OrgConstants.Role_NAME.SuperAdmin.name())) {
							    if(null == p) {
							        continue;
							    }
							} else if (null == p ||null == p.getEnterResourceId()|| p.getEnterResourceId() == 0L) {
								continue;
							}

						}
					}

					templist.add(privTreeNodeBO);
					// }
				}
				treeNodes4Front.clear();
				treeNodes4Front.addAll(templist);
			}
		}
	}

	*//**
	 * 
	 * @param privTreeNodeBO
	 * @param list
	 * @return
	 *//*
	private boolean isHaveChildNode(PrivTreeNodeBO privTreeNodeBO,
			List<PrivTreeNodeBO> list) {
		for (PrivTreeNodeBO privTreeNodeBO2 : list) {
			if (privTreeNodeBO2.getpIdKey().split("_")[1].equals(privTreeNodeBO
					.getIdKey().split("_")[1])) {
				return true;
			}

		}
		return false;
	}

	*//**
	 * @param fileManager
	 *            the fileManager to set
	 *//*
	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	*//**
	 * @param privilegeManage
	 *            the privilegeManage to set
	 *//*
	public void setPrivilegeManage(PrivilegeManage privilegeManage) {
		this.privilegeManage = privilegeManage;
	}*/
}
