local function translator(input, seg)
      if (input:sub(1, 1) == '\'') then
         local remove_head = string.sub(input, 2)
         local with_spaces = " " .. remove_head .. " "
         local with_grave_accents = " `" .. remove_head .. "` "
         yield(Candidate("ASCII", seg.start, seg._end, with_spaces, "空格"))
         yield(Candidate("ASCII", seg.start, seg._end, with_grave_accents, "反引"))
         yield(Candidate("ASCII", seg.start, seg._end, with_grave_accents, "原始"))
      end
 end
 
 return translator