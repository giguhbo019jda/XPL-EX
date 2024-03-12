function after(hook, param)
	local ret = param:getResult()
	if ret == nil then
		return false
	end

    local fakeString = param:getSetting("gsm.setting.network.opportunistic", "false")
    local fake = false
    if fakeString == "true"
        fake = true
    end

	param:setResult(fake)
	return true, tostring(ret), tostring(fake)
end