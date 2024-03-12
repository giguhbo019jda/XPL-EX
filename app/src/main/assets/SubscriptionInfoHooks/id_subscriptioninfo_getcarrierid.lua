function after(hook, param)
	local ret = param:getResult()
	if ret == nil then
		return false
	end

    --gsm.operator.id
    local fake = 274299
    local mcc = param:getSetting("gsm.operator.mcc", "274")
    local mnc = param:getSetting("gsm.operator.mnc", "299")

    local mccNumber = tonumber(mcc)
    local mncNumber = tonumber(mnc)
    if mccNumber ~= nil and mncNumber =~ nil then
        fake = mccNumber + mncNumber
    else
        local oId = param:getSetting("gsm.operator.id", "274299")
        local oIdNumber = tonumber(oId)
        if oIdNumber ~= nil then
            fake = oIdNumber
        end
    end

	param:setResult(fake)
	return true, tostring(ret), tostring(fake)
end