--getSubscriberId
function after(hook, param)
	local ret = param:getResult()
	if ret == nil then
		return false
	end

    local

    local fake = 274299
    local fakeString = param:getSetting("gsm.operator.id")
    local fakeNumber = tonumber(fakeString)
    if fakeNumber ~= nil then
        fake = fakeNumber
    else
        local mcc = param:getSettingReMap("gsm.operator.mcc", "phone.mcc", "274")
        local mnc = param:getSetting("gsm.operator.mnc", "phone.mnc", "299")
        local mccNumber = tonumber(mcc)
        local mncNumber = tonumber(mnc)

        if mccNumber ~= nil and mncNumber ~= nil then
            fake = mccNumber + mncNumber
        end
    end

    param:setResult(fake)
	return true, tostring(ret), tostring(fake)
end